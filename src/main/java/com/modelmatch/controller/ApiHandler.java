package com.modelmatch.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.modelmatch.classifier.TaskRequirementExtractor;
import com.modelmatch.classifier.TfIdfClassifier;
import com.modelmatch.engine.ExplainabilityService;
import com.modelmatch.engine.RecommendationEngine;
import com.modelmatch.model.*;
import com.modelmatch.repository.ModelDatabase;
import com.modelmatch.service.GeminiService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Handles all REST API requests for ModelMatch.
 */
public class ApiHandler implements HttpHandler {

    private final ModelDatabase modelDatabase;
    private final TfIdfClassifier tfIdfClassifier;
    private final RecommendationEngine recommendationEngine;
    private final ExplainabilityService explainabilityService;
    private final GeminiService geminiService;
    private final Gson gson;

    public ApiHandler(ModelDatabase modelDatabase,
                      TfIdfClassifier tfIdfClassifier,
                      RecommendationEngine recommendationEngine,
                      ExplainabilityService explainabilityService,
                      GeminiService geminiService) {
        this.modelDatabase = modelDatabase;
        this.tfIdfClassifier = tfIdfClassifier;
        this.recommendationEngine = recommendationEngine;
        this.explainabilityService = explainabilityService;
        this.geminiService = geminiService;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers for web client
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if ("/api/recommend".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleRecommend(exchange);
            } else if ("/api/classify".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleClassify(exchange);
            } else if ("/api/models".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetModels(exchange);
            } else if (path.startsWith("/api/models/") && "GET".equalsIgnoreCase(method)) {
                handleGetSingleModel(exchange, path.substring("/api/models/".length()));
            } else if ("/api/compare".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleCompareModels(exchange);
            } else if ("/api/health".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleHealth(exchange);
            } else if ("/api/config".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleConfig(exchange);
            } else {
                sendJsonResponse(exchange, 404, Map.of("error", "API endpoint not found: " + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, Map.of("error", "Internal Server Error: " + e.getMessage()));
        }
    }

    private void handleRecommend(HttpExchange exchange) throws IOException {
        long startTime = System.currentTimeMillis();
        String body = readRequestBody(exchange);
        RecommendationRequest request = gson.fromJson(body, RecommendationRequest.class);

        if (request == null || request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            sendJsonResponse(exchange, 400, Map.of("error", "Missing prompt parameter"));
            return;
        }

        String prompt = request.getPrompt().trim();

        // 1. TF-IDF Classification
        TfIdfClassifier.ClassificationResult classResult = tfIdfClassifier.classify(prompt);
        TaskCategory category = classResult.getCategory();

        // Allow manual category override if requested
        if (request.getPreferredCategory() != null && !request.getPreferredCategory().isEmpty()) {
            category = TaskCategory.fromString(request.getPreferredCategory());
        }

        // 2. Extract quantitative requirements vector
        TaskRequirements req = TaskRequirementExtractor.extractRequirements(prompt, category);
        if (request.getEstimatedInputTokens() != null && request.getEstimatedInputTokens() > 0) {
            req.setEstimatedInputTokens(request.getEstimatedInputTokens());
        }
        if (request.getEstimatedOutputTokens() != null && request.getEstimatedOutputTokens() > 0) {
            req.setEstimatedOutputTokens(request.getEstimatedOutputTokens());
        }

        // 3. Determine active weights (user priority sliders or adaptive derivation)
        PriorityWeights weights;
        if (request.getCustomWeights() != null) {
            weights = request.getCustomWeights();
            weights.normalize();
        } else {
            weights = PriorityWeights.fromRequirements(req);
        }

        // 4. Recommendation & Ranking
        List<ModelRankDetail> ranked = recommendationEngine.rankModels(req, weights);
        ModelRankDetail topModel = ranked.get(0);
        List<ModelRankDetail> alternatives = ranked.subList(1, Math.min(6, ranked.size()));

        // 5. Academic Explainability
        String whyRec = explainabilityService.generateWhyRecommended(topModel, req);
        String drawbacks = explainabilityService.generateDrawbacks(topModel);
        String formula = explainabilityService.generateFormulaExplanation(topModel, req, weights);

        // 6. Gemini 3.5 Flash deep commentary
        String geminiInsights = "";
        boolean isGeminiUsed = Boolean.TRUE.equals(request.getUseGeminiAnalysis());
        if (isGeminiUsed) {
            geminiInsights = geminiService.generatePersonalizedExplanation(prompt, req, topModel, request.getUserApiKey());
        }

        long duration = System.currentTimeMillis() - startTime;

        RecommendationResponse resp = new RecommendationResponse();
        resp.setQueryPrompt(prompt);
        resp.setClassifiedCategory(category);
        resp.setClassificationConfidence(classResult.getConfidence());
        resp.setCategoryDistribution(classResult.getDistribution());
        resp.setExtractedRequirements(req);
        resp.setActiveWeights(weights);
        resp.setTopModel(topModel);
        resp.setRankedAlternatives(alternatives);
        resp.setWhyRecommended(whyRec);
        resp.setPotentialDrawbacks(drawbacks);
        resp.setFormulaBreakdown(formula);
        resp.setGeminiAiInsights(geminiInsights);
        resp.setGeminiPowered(isGeminiUsed);
        resp.setExecutionTimeMs(duration);
        resp.setEngineInfo("ModelMatch 1.0 (Java 21 LTS + TF-IDF Classifier + Cosine Similarity + Gemini 3.5 Flash)");

        sendJsonResponse(exchange, 200, resp);
    }

    private void handleClassify(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        RecommendationRequest request = gson.fromJson(body, RecommendationRequest.class);
        if (request == null || request.getPrompt() == null) {
            sendJsonResponse(exchange, 400, Map.of("error", "Missing prompt"));
            return;
        }

        TfIdfClassifier.ClassificationResult result = tfIdfClassifier.classify(request.getPrompt());
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("category", result.getCategory().getDisplayName());
        resp.put("description", result.getCategory().getDescription());
        resp.put("confidence", result.getConfidence());
        resp.put("distribution", result.getDistribution());

        sendJsonResponse(exchange, 200, resp);
    }

    private void handleGetModels(HttpExchange exchange) throws IOException {
        sendJsonResponse(exchange, 200, modelDatabase.getAllModels());
    }

    private void handleGetSingleModel(HttpExchange exchange, String id) throws IOException {
        Optional<ModelEntity> opt = modelDatabase.getModelById(id);
        if (opt.isPresent()) {
            sendJsonResponse(exchange, 200, opt.get());
        } else {
            sendJsonResponse(exchange, 404, Map.of("error", "Model not found: " + id));
        }
    }

    private void handleCompareModels(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        JsonObject obj = gson.fromJson(body, JsonObject.class);
        List<ModelEntity> compared = new ArrayList<>();
        if (obj != null && obj.has("modelIds")) {
            for (var elem : obj.getAsJsonArray("modelIds")) {
                modelDatabase.getModelById(elem.getAsString()).ifPresent(compared::add);
            }
        }
        sendJsonResponse(exchange, 200, compared);
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("service", "ModelMatch Recommendation Engine");
        health.put("javaVersion", System.getProperty("java.version"));
        health.put("modelsLoaded", modelDatabase.getAllModels().size());
        health.put("geminiStatus", geminiService.isConfigured() ? "Active (Gemini 3.5 Flash)" : "Standby (Local TF-IDF Mode)");
        health.put("geminiConfigured", geminiService.isConfigured());
        sendJsonResponse(exchange, 200, health);
    }

    private void handleConfig(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        JsonObject obj = gson.fromJson(body, JsonObject.class);
        if (obj != null && obj.has("apiKey")) {
            geminiService.setApiKey(obj.get("apiKey").getAsString());
            sendJsonResponse(exchange, 200, Map.of("status", "success", "message", "API key updated"));
        } else {
            sendJsonResponse(exchange, 400, Map.of("error", "Missing apiKey field"));
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        byte[] bytes = gson.toJson(data).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
