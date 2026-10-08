package com.modelmatch.engine;

import com.modelmatch.model.*;
import com.modelmatch.repository.ModelDatabase;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core Recommendation Engine implementing:
 * 1. Feature Vector Cosine Similarity
 * 2. User Priority Weighted Scoring (w_r*R + w_c*C + w_x*X + w_m*M + w_s*S + w_p*P)
 * 3. Context & Multimodal Constraint Verification
 * 4. Hybrid Multi-Criteria Ranking
 */
public class RecommendationEngine {

    private final ModelDatabase modelDatabase;

    public RecommendationEngine(ModelDatabase modelDatabase) {
        this.modelDatabase = modelDatabase;
    }

    /**
     * Executes the complete recommendation and ranking pipeline.
     */
    public List<ModelRankDetail> rankModels(TaskRequirements req, PriorityWeights weights) {
        weights.normalize();
        double[] userVec = req.getFeatureVector();

        List<ModelEntity> allModels = modelDatabase.getAllModels();
        List<ModelRankDetail> scoredModels = new ArrayList<>();

        for (ModelEntity model : allModels) {
            double[] modelVec = model.getFeatureVector();

            // 1. Calculate Cosine Similarity: (User · Model) / (|User| * |Model|)
            double cosineSim = computeCosineSimilarity(userVec, modelVec);

            // 2. Calculate Weighted Priority Score:
            // Score = w_r*R + w_c*C + w_x*X + w_m*M + w_s*S + w_p*P
            double weightedScore = (
                weights.getWeightReasoning() * model.getReasoningScore() +
                weights.getWeightCoding() * model.getCodingScore() +
                weights.getWeightContext() * model.getContextScore() +
                weights.getWeightMultimodal() * model.getMultimodalScore() +
                weights.getWeightSpeed() * model.getSpeedScore() +
                weights.getWeightCost() * model.getCostScore()
            );

            // 3. Apply Hard Constraint Penalties
            double constraintPenaltyMultiplier = 1.0;

            // Multimodal hard constraint: if task strongly requires vision (>0.60) and model is text-only
            if (req.getMultimodal() > 0.60 && model.getMultimodalScore() < 30.0) {
                constraintPenaltyMultiplier *= 0.65; // Severe penalty for inability to process images
            }

            // Context window constraint: if user task demands massive context (>0.80) and model < 200k tokens
            if (req.getContext() > 0.80 && model.getContextWindowTokens() < 200000) {
                constraintPenaltyMultiplier *= 0.85;
            }

            // 4. Combined Hybrid Final Score (0 - 100 scale)
            double cosinePercentage = cosineSim * 100.0;
            double finalScore = ((0.50 * weightedScore) + (0.50 * cosinePercentage)) * constraintPenaltyMultiplier;
            finalScore = Math.max(10.0, Math.min(99.4, finalScore)); // realistic percentage ceiling

            // Round to 1 decimal place
            finalScore = Math.round(finalScore * 10.0) / 10.0;
            weightedScore = Math.round(weightedScore * 10.0) / 10.0;
            cosineSim = Math.round(cosineSim * 1000.0) / 1000.0;

            // 5. Calculate Estimated Cost for user's token volume
            double estimatedCost = calculateQueryCost(model, req.getEstimatedInputTokens(), req.getEstimatedOutputTokens());

            // 6. Generate match highlights and potential drawbacks
            List<String> highlights = generateHighlights(model, req, weights);
            List<String> drawbacks = generateDrawbacks(model, req);
            String rationale = buildSummaryRationale(model, finalScore, req);

            scoredModels.add(new ModelRankDetail(
                0, model, finalScore, cosineSim, weightedScore, estimatedCost, highlights, drawbacks, rationale
            ));
        }

        // Sort descending by finalScore
        scoredModels.sort((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()));

        // Assign rank numbers 1, 2, 3...
        for (int i = 0; i < scoredModels.size(); i++) {
            scoredModels.get(i).setRank(i + 1);
        }

        return scoredModels;
    }

    /**
     * Vector Cosine Similarity:
     * Similarity(A, B) = (A · B) / (||A|| * ||B||)
     */
    public double computeCosineSimilarity(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length) return 0.0;
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        double denom = Math.sqrt(normA) * Math.sqrt(normB);
        if (denom < 1e-9) return 0.0;
        return dot / denom;
    }

    /**
     * Calculates query dollar cost based on model pricing per 1M tokens.
     */
    public double calculateQueryCost(ModelEntity model, int inputTokens, int outputTokens) {
        double inputCost = (inputTokens / 1_000_000.0) * model.getInputCostPer1M();
        double outputCost = (outputTokens / 1_000_000.0) * model.getOutputCostPer1M();
        double total = inputCost + outputCost;
        return Math.round(total * 10000.0) / 10000.0; // 4 decimals
    }

    private List<String> generateHighlights(ModelEntity m, TaskRequirements req, PriorityWeights w) {
        List<String> list = new ArrayList<>();
        if (req.getCoding() > 0.70 && m.getCodingScore() >= 90.0) {
            list.add(String.format("Exceptional coding capability (score: %.0f/100) excels at code generation and refactoring", m.getCodingScore()));
        }
        if (req.getReasoning() > 0.70 && m.getReasoningScore() >= 90.0) {
            list.add(String.format("Premier reasoning performance (score: %.0f/100) for deep mathematical and architectural logic", m.getReasoningScore()));
        }
        if (req.getContext() > 0.70 && m.getContextWindowTokens() >= 500000) {
            list.add(String.format("Massive context window (%s tokens) easily absorbs entire codebases and long documents", formatTokens(m.getContextWindowTokens())));
        }
        if (req.getMultimodal() > 0.50 && m.getMultimodalScore() >= 90.0) {
            list.add(String.format("High multimodal fidelity (score: %.0f/100) parses screenshots, diagrams, and complex charts", m.getMultimodalScore()));
        }
        if (req.getCost() > 0.70 && m.getCostScore() >= 85.0) {
            list.add(String.format("Outstanding cost-efficiency ($%.3f / 1M input) ideal for your limited budget constraint", m.getInputCostPer1M()));
        }
        if (req.getSpeed() > 0.70 && m.getSpeedScore() >= 85.0) {
            list.add(String.format("Blazing response speed (~%d tokens/sec) delivers sub-second conversational latency", m.getSpeedTokensPerSec()));
        }

        if (list.isEmpty()) {
            list.addAll(m.getStrengths());
        }
        return list;
    }

    private List<String> generateDrawbacks(ModelEntity m, TaskRequirements req) {
        List<String> drawbacks = new ArrayList<>();
        if (req.getCost() > 0.70 && m.getCostScore() < 70.0) {
            drawbacks.add(String.format("Higher inference pricing ($%.2f / 1M output) may exceed strict budget limits for high-volume jobs", m.getOutputCostPer1M()));
        }
        if (req.getSpeed() > 0.70 && m.getSpeedScore() < 75.0) {
            drawbacks.add(String.format("Generation speed (~%d tok/s) is lower than dedicated flash/realtime models", m.getSpeedTokensPerSec()));
        }
        if (req.getMultimodal() > 0.50 && m.getMultimodalScore() < 40.0) {
            drawbacks.add("Lacks native visual processing; cannot ingest images, charts, or video directly");
        }
        if (req.getContext() > 0.75 && m.getContextWindowTokens() <= 128000) {
            drawbacks.add(String.format("Context window limited to %s tokens; might require chunking for massive repos", formatTokens(m.getContextWindowTokens())));
        }

        if (drawbacks.isEmpty() && !m.getWeaknesses().isEmpty()) {
            drawbacks.add(m.getWeaknesses().get(0));
        }
        return drawbacks;
    }

    private String buildSummaryRationale(ModelEntity m, double score, TaskRequirements req) {
        return String.format(
            "%s achieved a %.1f%% compatibility score. It matches your %s requirements with balanced capability across reasoning (%.0f), coding (%.0f), and context handling.",
            m.getName(), score, req.getTaskCategory().getDisplayName(), m.getReasoningScore(), m.getCodingScore()
        );
    }

    private String formatTokens(int tokens) {
        if (tokens >= 1_000_000) return (tokens / 1_000_000) + "M";
        if (tokens >= 1_000) return (tokens / 1_000) + "K";
        return String.valueOf(tokens);
    }
}
