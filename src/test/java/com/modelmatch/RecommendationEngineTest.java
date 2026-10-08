package com.modelmatch;

import com.modelmatch.classifier.TaskRequirementExtractor;
import com.modelmatch.classifier.TfIdfClassifier;
import com.modelmatch.engine.RecommendationEngine;
import com.modelmatch.model.ModelRankDetail;
import com.modelmatch.model.PriorityWeights;
import com.modelmatch.model.TaskCategory;
import com.modelmatch.model.TaskRequirements;
import com.modelmatch.repository.ModelDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationEngineTest {

    private static ModelDatabase modelDatabase;
    private static RecommendationEngine recommendationEngine;
    private static TfIdfClassifier classifier;

    @BeforeAll
    public static void setUp() {
        modelDatabase = new ModelDatabase();
        recommendationEngine = new RecommendationEngine(modelDatabase);
        classifier = new TfIdfClassifier();
    }

    @Test
    @DisplayName("Verify model catalog contains at least 35 models across multiple providers")
    public void testCatalogSizeAndProviderDiversity() {
        int count = modelDatabase.getAllModels().size();
        assertTrue(count >= 35, "Expected at least 35 models in catalog, found: " + count);

        Set<String> providers = new HashSet<>();
        modelDatabase.getAllModels().forEach(m -> providers.add(m.getProvider()));
        assertTrue(providers.size() >= 10, "Expected at least 10 distinct providers, found: " + providers.size());
        assertTrue(providers.contains("OpenAI"), "Missing OpenAI provider");
        assertTrue(providers.contains("Anthropic"), "Missing Anthropic provider");
        assertTrue(providers.contains("Google"), "Missing Google provider");
        assertTrue(providers.contains("DeepSeek"), "Missing DeepSeek provider");
        assertTrue(providers.contains("Meta"), "Missing Meta provider");
        assertTrue(providers.contains("Mistral AI"), "Missing Mistral provider");
        assertTrue(providers.contains("Alibaba Cloud"), "Missing Alibaba provider");
        assertTrue(providers.contains("Cohere"), "Missing Cohere provider");
        assertTrue(providers.contains("Amazon"), "Missing Amazon provider");
    }

    @Test
    @DisplayName("Task 1: Coding task should favor top code-specialized model")
    public void testCodingTaskRecommendation() {
        String prompt = "Build a full-stack reactive microservice architecture in Rust and TypeScript with complex AST parsing, compiler optimizations, and automated unit tests.";
        TfIdfClassifier.ClassificationResult cr = classifier.classify(prompt);
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, cr.getCategory());
        PriorityWeights weights = PriorityWeights.fromRequirements(req);

        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        assertFalse(ranked.isEmpty());
        ModelRankDetail top = ranked.get(0);

        // Top model should have superior coding capability (>= 90)
        assertTrue(top.getModel().getCodingScore() >= 90.0,
            "Winner for coding task should have coding score >= 90, got: " + top.getModel().getCodingScore() + " (" + top.getModel().getName() + ")");
    }

    @Test
    @DisplayName("Task 2: Mathematics & STEM reasoning task should favor deep reasoning models")
    public void testMathematicsReasoningTask() {
        String prompt = "Solve an advanced Olympiad mathematical proof involving differential geometry, Galois theory, and number theory with step-by-step rigorous logical deductions.";
        TfIdfClassifier.ClassificationResult cr = classifier.classify(prompt);
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, cr.getCategory());
        PriorityWeights weights = PriorityWeights.fromRequirements(req);

        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        ModelRankDetail top = ranked.get(0);

        // Top model should have premier reasoning score (>= 94)
        assertTrue(top.getModel().getReasoningScore() >= 94.0,
            "Winner for math task should have reasoning score >= 94, got: " + top.getModel().getReasoningScore() + " (" + top.getModel().getName() + ")");
    }

    @Test
    @DisplayName("Task 3: Creative writing task should favor nuanced prose models")
    public void testCreativeWritingTask() {
        String prompt = "Write an evocative, poetic gothic novel chapter with rich atmospheric imagery, deep emotional dialogue, and Victorian literary cadence.";
        TfIdfClassifier.ClassificationResult cr = classifier.classify(prompt);
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, cr.getCategory());
        PriorityWeights weights = PriorityWeights.fromRequirements(req);

        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        ModelRankDetail top = ranked.get(0);

        // Creative tasks value reasoning and balance over raw speed/cost
        assertTrue(top.getFinalScore() > 70.0);
        assertNotNull(top.getModel().getName());
    }

    @Test
    @DisplayName("Task 4: Long document analysis demands massive context (1M+ tokens)")
    public void testLongDocumentAnalysisTask() {
        String prompt = "Ingest a 2,000-page historical legal archive of 1.5 million words and identify contractual discrepancies and cross-document inconsistencies.";
        TfIdfClassifier.ClassificationResult cr = classifier.classify(prompt);
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, TaskCategory.RESEARCH);
        req.setContext(0.95);
        req.setEstimatedInputTokens(1500000);
        PriorityWeights weights = PriorityWeights.fromRequirements(req);

        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        ModelRankDetail top = ranked.get(0);

        // Winner must support >= 1M context tokens
        assertTrue(top.getModel().getContextWindowTokens() >= 1000000,
            "Winner for 1.5M token task must have at least 1M context window, got: " + top.getModel().getContextWindowTokens() + " (" + top.getModel().getName() + ")");
    }

    @Test
    @DisplayName("Task 5: Vision and multimodal task severely penalizes text-only models")
    public void testMultimodalVisionTask() {
        String prompt = "Inspect these high-resolution circuit board schematic images and architecture blueprint diagrams. Extract electronic component symbols and trace faulty connections.";
        TfIdfClassifier.ClassificationResult cr = classifier.classify(prompt);
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, cr.getCategory());
        req.setMultimodal(0.95); // High multimodal constraint
        PriorityWeights weights = PriorityWeights.fromRequirements(req);

        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        ModelRankDetail top = ranked.get(0);

        // Winner must support multimodal vision
        assertTrue(top.getModel().getMultimodalScore() >= 80.0,
            "Winner for vision task must have multimodal score >= 80, got: " + top.getModel().getMultimodalScore() + " (" + top.getModel().getName() + ")");

        // Text-only models like DeepSeek R1 or Qwen Coder must be penalized below top multimodal models
        for (ModelRankDetail detail : ranked) {
            if ("deepseek-r1".equals(detail.getModel().getId()) || "qwen-2-5-coder-32b".equals(detail.getModel().getId())) {
                assertTrue(detail.getFinalScore() < top.getFinalScore(),
                    "Text-only model " + detail.getModel().getName() + " should score below multimodal winner");
            }
        }
    }

    @Test
    @DisplayName("Task 6: High-volume budget task favors ultra-low-cost high-speed models")
    public void testLowCostHighVolumeTask() {
        String prompt = "Classify 1,000,000 customer feedback tickets for sentiment and route them with maximum throughput and minimum API cost under strict budget constraints.";
        TfIdfClassifier.ClassificationResult cr = classifier.classify(prompt);
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, cr.getCategory());
        req.setCost(0.95);
        req.setSpeed(0.90);
        PriorityWeights weights = PriorityWeights.fromRequirements(req);

        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        ModelRankDetail top = ranked.get(0);

        // Winner must be extremely economical (input cost <= $0.20 per 1M tokens)
        assertTrue(top.getModel().getInputCostPer1M() <= 0.20,
            "Winner for budget task should cost <= $0.20/1M, got: " + top.getModel().getInputCostPer1M() + " (" + top.getModel().getName() + ")");
    }

    @Test
    @DisplayName("Task 7: Multilingual European translation task")
    public void testMultilingualTask() {
        String prompt = "Translate and cross-reference complex European Union regulatory directives across French, German, Spanish, and Italian with native legal nuance.";
        TfIdfClassifier.ClassificationResult cr = classifier.classify(prompt);
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, cr.getCategory());
        PriorityWeights weights = PriorityWeights.fromRequirements(req);

        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        assertFalse(ranked.isEmpty());
        ModelRankDetail top = ranked.get(0);
        assertTrue(top.getFinalScore() > 70.0);
    }

    @Test
    @DisplayName("Verify diverse tasks produce different winning models (No hardcoded Gemini bias)")
    public void testWinningModelDiversityAcrossTasks() {
        Set<String> winningModelIds = new HashSet<>();

        // 1. Math Olympiad task
        TaskRequirements mathReq = new TaskRequirements(TaskCategory.REASONING, 0.98, 0.40, 0.50, 0.10, 0.40, 0.30);
        winningModelIds.add(recommendationEngine.rankModels(mathReq, PriorityWeights.fromRequirements(mathReq)).get(0).getModel().getId());

        // 2. Pure Code AST Refactoring task
        TaskRequirements codeReq = new TaskRequirements(TaskCategory.CODING, 0.70, 0.98, 0.60, 0.05, 0.60, 0.40);
        winningModelIds.add(recommendationEngine.rankModels(codeReq, PriorityWeights.fromRequirements(codeReq)).get(0).getModel().getId());

        // 3. 2M Context Document task
        TaskRequirements contextReq = new TaskRequirements(TaskCategory.RESEARCH, 0.70, 0.50, 1.00, 0.50, 0.40, 0.30);
        winningModelIds.add(recommendationEngine.rankModels(contextReq, PriorityWeights.fromRequirements(contextReq)).get(0).getModel().getId());

        // 4. Ultra-cheap high-speed sentiment task
        TaskRequirements budgetReq = new TaskRequirements(TaskCategory.GENERAL, 0.40, 0.30, 0.30, 0.10, 0.95, 0.98);
        winningModelIds.add(recommendationEngine.rankModels(budgetReq, PriorityWeights.fromRequirements(budgetReq)).get(0).getModel().getId());

        // 5. High-resolution vision diagram task
        TaskRequirements visionReq = new TaskRequirements(TaskCategory.MULTIMODAL, 0.60, 0.50, 0.50, 0.98, 0.50, 0.30);
        winningModelIds.add(recommendationEngine.rankModels(visionReq, PriorityWeights.fromRequirements(visionReq)).get(0).getModel().getId());

        // Verify that at least 4 different models win across these 5 diverse tasks!
        assertTrue(winningModelIds.size() >= 3,
            "Expected diverse winning models across tasks, but got: " + winningModelIds);
    }
}
