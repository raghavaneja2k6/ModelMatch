package com.modelmatch.model;

/**
 * Incoming request payload for LLM recommendation.
 */
public class RecommendationRequest {
    private String prompt;
    private PriorityWeights customWeights;
    private String preferredCategory; // Optional manual override
    private Integer estimatedInputTokens;
    private Integer estimatedOutputTokens;
    private Boolean useGeminiAnalysis = true; // Use Gemini 3.5 Flash for deep reasoning
    private String userApiKey; // Optional ephemeral user-provided API key (never stored or logged)

    public RecommendationRequest() {}

    public RecommendationRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public PriorityWeights getCustomWeights() { return customWeights; }
    public void setCustomWeights(PriorityWeights customWeights) { this.customWeights = customWeights; }

    public String getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(String preferredCategory) { this.preferredCategory = preferredCategory; }

    public Integer getEstimatedInputTokens() { return estimatedInputTokens; }
    public void setEstimatedInputTokens(Integer estimatedInputTokens) { this.estimatedInputTokens = estimatedInputTokens; }

    public Integer getEstimatedOutputTokens() { return estimatedOutputTokens; }
    public void setEstimatedOutputTokens(Integer estimatedOutputTokens) { this.estimatedOutputTokens = estimatedOutputTokens; }

    public Boolean getUseGeminiAnalysis() { return useGeminiAnalysis; }
    public void setUseGeminiAnalysis(Boolean useGeminiAnalysis) { this.useGeminiAnalysis = useGeminiAnalysis; }

    public String getUserApiKey() { return userApiKey; }
    public void setUserApiKey(String userApiKey) { this.userApiKey = userApiKey; }
}
