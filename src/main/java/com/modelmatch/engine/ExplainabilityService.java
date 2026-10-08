package com.modelmatch.engine;

import com.modelmatch.model.*;

import java.util.List;

/**
 * Generates transparent, human-readable, and academic mathematical
 * explanations for why a specific model was recommended.
 */
public class ExplainabilityService {

    /**
     * Constructs a step-by-step academic formula breakdown string.
     */
    public String generateFormulaExplanation(ModelRankDetail topRank, TaskRequirements req, PriorityWeights w) {
        ModelEntity m = topRank.getModel();
        double[] uVec = req.getFeatureVector();
        double[] mVec = m.getFeatureVector();

        StringBuilder sb = new StringBuilder();
        sb.append("### Mathematical Recommendation Derivation\n\n");

        sb.append("**1. Extracted Requirement Vector (User $U$):**\n");
        sb.append(String.format("- Reasoning ($R$): `%.2f`\n", uVec[0]));
        sb.append(String.format("- Coding ($C$): `%.2f`\n", uVec[1]));
        sb.append(String.format("- Context ($X$): `%.2f`\n", uVec[2]));
        sb.append(String.format("- Multimodal ($M$): `%.2f`\n", uVec[3]));
        sb.append(String.format("- Speed ($S$): `%.2f`\n", uVec[4]));
        sb.append(String.format("- Cost Efficiency ($P$): `%.2f`\n\n", uVec[5]));

        sb.append("**2. Model Capability Vector ($M_{model}$):**\n");
        sb.append(String.format("- Reasoning: `%.2f` (Score: %.0f/100)\n", mVec[0], m.getReasoningScore()));
        sb.append(String.format("- Coding: `%.2f` (Score: %.0f/100)\n", mVec[1], m.getCodingScore()));
        sb.append(String.format("- Context: `%.2f` (Score: %.0f/100)\n", mVec[2], m.getContextScore()));
        sb.append(String.format("- Multimodal: `%.2f` (Score: %.0f/100)\n", mVec[3], m.getMultimodalScore()));
        sb.append(String.format("- Speed: `%.2f` (Score: %.0f/100)\n", mVec[4], m.getSpeedScore()));
        sb.append(String.format("- Cost Efficiency: `%.2f` (Score: %.0f/100)\n\n", mVec[5], m.getCostScore()));

        sb.append("**3. Vector Cosine Similarity:**\n");
        sb.append("$$\\text{Similarity}(U, M) = \\frac{U \\cdot M}{\\|U\\| \\|M\\|} = ");
        sb.append(String.format("%.4f$$\n\n", topRank.getCosineSimilarity()));

        sb.append("**4. User Priority Weighted Multi-Criteria Function:**\n");
        sb.append("$$\\text{Score} = w_r R + w_c C + w_x X + w_m M + w_s S + w_p P$$\n");
        sb.append(String.format(
            "$$\\text{Score} = (%.2f \\times %.0f) + (%.2f \\times %.0f) + (%.2f \\times %.0f) + (%.2f \\times %.0f) + (%.2f \\times %.0f) + (%.2f \\times %.0f) = \\mathbf{%.1f}$$\n\n",
            w.getWeightReasoning(), m.getReasoningScore(),
            w.getWeightCoding(), m.getCodingScore(),
            w.getWeightContext(), m.getContextScore(),
            w.getWeightMultimodal(), m.getMultimodalScore(),
            w.getWeightSpeed(), m.getSpeedScore(),
            w.getWeightCost(), m.getCostScore(),
            topRank.getWeightedScore()
        ));

        sb.append("**5. Hybrid Synthesis Final Ranking:**\n");
        sb.append(String.format(
            "$$\\text{Final Match} = (0.50 \\times \\text{WeightedScore}) + (0.50 \\times \\text{Cosine\\%%}) = \\mathbf{%.1f\\%%}$$\n",
            topRank.getFinalScore()
        ));

        return sb.toString();
    }

    /**
     * Formats natural language explanation bullet points.
     */
    public String generateWhyRecommended(ModelRankDetail topRank, TaskRequirements req) {
        StringBuilder sb = new StringBuilder();
        ModelEntity m = topRank.getModel();
        sb.append(String.format("**Recommended: %s**\n", m.getName()));
        sb.append(String.format("%s received a top compatibility score of **%.1f%%** for your requirements.\n\n", m.getName(), topRank.getFinalScore()));
        sb.append("**Why this model?**\n");
        for (String highlight : topRank.getMatchHighlights()) {
            sb.append("- ").append(highlight).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Formats drawback section.
     */
    public String generateDrawbacks(ModelRankDetail topRank) {
        if (topRank.getPotentialDrawbacks().isEmpty()) {
            return "No significant drawbacks detected relative to your stated constraints.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("**Potential Tradeoffs & Watch-outs:**\n");
        for (String drawback : topRank.getPotentialDrawbacks()) {
            sb.append("- ").append(drawback).append("\n");
        }
        return sb.toString().trim();
    }
}
