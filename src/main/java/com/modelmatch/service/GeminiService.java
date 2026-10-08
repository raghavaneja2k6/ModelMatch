package com.modelmatch.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.modelmatch.model.ModelRankDetail;
import com.modelmatch.model.TaskRequirements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service integrating Google Generative AI (Gemini 3.5 Flash).
 * Obtains the API key exclusively from server environment variable GEMINI_API_KEY,
 * JVM system property gemini.api.key, or a local gitignored .env file.
 * Handles intelligent task analysis and deep natural language explainability.
 */
public class GeminiService {

    private static final Logger LOGGER = Logger.getLogger(GeminiService.class.getName());
    private static final String MODEL_ID = "gemini-3.5-flash";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_ID + ":generateContent?key=";

    private final HttpClient httpClient;
    private final Gson gson;
    private String serverApiKey;

    public GeminiService() {
        this.serverApiKey = resolveApiKey();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.gson = new Gson();
    }

    /**
     * Resolves the API key from:
     * 1. Server Environment Variable: GEMINI_API_KEY
     * 2. JVM System Property: gemini.api.key
     * 3. Local gitignored .env file (for local development convenience)
     */
    private static String resolveApiKey() {
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey.trim();
        }

        String propKey = System.getProperty("gemini.api.key");
        if (propKey != null && !propKey.trim().isEmpty()) {
            return propKey.trim();
        }

        // Check local gitignored .env file
        File envFile = new File(".env");
        if (envFile.exists() && envFile.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("#") || line.isEmpty()) continue;
                    if (line.startsWith("GEMINI_API_KEY=")) {
                        String val = line.substring("GEMINI_API_KEY=".length()).trim();
                        if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                            val = val.substring(1, val.length() - 1).trim();
                        }
                        if (!val.isEmpty() && !val.equals("your_gemini_api_key_here")) {
                            return val;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

    public synchronized void setApiKey(String key) {
        if (key != null && !key.trim().isEmpty()) {
            this.serverApiKey = key.trim();
        }
    }

    public boolean isConfigured() {
        return serverApiKey != null && !serverApiKey.trim().isEmpty();
    }

    public String getApiKeyMasked() {
        if (serverApiKey == null || serverApiKey.trim().isEmpty()) {
            return "Not Configured";
        }
        return "Configured (Server Env)";
    }

    public String generatePersonalizedExplanation(String userPrompt, TaskRequirements req, ModelRankDetail topModel) {
        return generatePersonalizedExplanation(userPrompt, req, topModel, null);
    }

    /**
     * Calls Gemini 3.5 Flash to generate an intelligent, context-aware analysis
     * explaining why the chosen model is ideal for the user's specific prompt.
     * Supports optional ephemeral userApiKey (used only for this call, never saved or logged).
     */
    public String generatePersonalizedExplanation(String userPrompt, TaskRequirements req, ModelRankDetail topModel, String ephemeralUserKey) {
        String effectiveKey = (ephemeralUserKey != null && !ephemeralUserKey.trim().isEmpty())
            ? ephemeralUserKey.trim()
            : this.serverApiKey;

        if (effectiveKey == null || effectiveKey.trim().isEmpty()) {
            return generateFallbackExplanation(req, topModel);
        }

        try {
            String systemInstruction =
                "You are ModelMatch's AI Architect. The user submitted a task request. " +
                "ModelMatch's recommendation engine selected a top model. " +
                "Write a concise, professional 2-3 paragraph technical explanation analyzing why this model is optimal, " +
                "how its specific architecture benefits the user's workflow, and practical tips for implementation.";

            String userContent = String.format(
                "User Task: \"%s\"\n" +
                "Detected Task Category: %s\n" +
                "Extracted Requirements: Reasoning=%.2f, Coding=%.2f, Context=%.2f, Multimodal=%.2f, Speed=%.2f, Cost=%.2f\n" +
                "Top Recommended Model: %s (by %s)\n" +
                "Model Match Score: %.1f%%\n" +
                "Model Benchmark Source: %s\n\n" +
                "Please provide a crisp technical assessment of this match.",
                userPrompt,
                req.getTaskCategory().getDisplayName(),
                req.getReasoning(), req.getCoding(), req.getContext(), req.getMultimodal(), req.getSpeed(), req.getCost(),
                topModel.getModel().getName(), topModel.getModel().getProvider(),
                topModel.getFinalScore(),
                topModel.getModel().getBenchmarkSource()
            );

            // Construct JSON request
            JsonObject root = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject contentObj = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject partObj = new JsonObject();
            partObj.addProperty("text", systemInstruction + "\n\n" + userContent);
            parts.add(partObj);
            contentObj.add("parts", parts);
            contents.add(contentObj);
            root.add("contents", contents);

            String requestBody = gson.toJson(root);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + effectiveKey))
                .timeout(Duration.ofSeconds(12))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                if (candidates != null && candidates.size() > 0) {
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = firstCandidate.getAsJsonObject("content");
                    if (content != null) {
                        JsonArray outParts = content.getAsJsonArray("parts");
                        if (outParts != null && outParts.size() > 0) {
                            return outParts.get(0).getAsJsonObject().get("text").getAsString();
                        }
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Gemini API returned HTTP status {0}", response.statusCode());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Gemini API call failed or timed out: {0}", e.getMessage());
        }

        return generateFallbackExplanation(req, topModel);
    }

    private String generateFallbackExplanation(TaskRequirements req, ModelRankDetail topModel) {
        return String.format(
            "Based on our algorithmic multi-criteria analysis, %s provides the strongest balance for your %s requirements. " +
            "Its verified benchmark scores (%s) directly align with your need for %.0f%% reasoning and %.0f%% coding proficiency while respecting your constraints.",
            topModel.getModel().getName(),
            req.getTaskCategory().getDisplayName(),
            topModel.getModel().getBenchmarkSource(),
            req.getReasoning() * 100,
            req.getCoding() * 100
        );
    }
}
