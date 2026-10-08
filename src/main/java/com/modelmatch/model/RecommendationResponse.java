package com.modelmatch.model;

import java.util.List;
import java.util.Map;

/**
 * Complete recommendation response returned by the ModelMatch engine.
 */
public class RecommendationResponse {
    private String queryPrompt;
    private TaskCategory classifiedCategory;
    private double classificationConfidence;
    private Map<String, Double> categoryDistribution;
    private TaskRequirements extractedRequirements;
    private PriorityWeights activeWeights;
    
    // Recommendations
    private ModelRankDetail topModel;
    private List<ModelRankDetail> rankedAlternatives;
    
    // Academic & Explainability Breakdown
    private String whyRecommended;
    private String potentialDrawbacks;
    private String formulaBreakdown;
    private String geminiAiInsights;
    private boolean geminiPowered;
    private long executionTimeMs;
    private String engineInfo;

    public RecommendationResponse() {}

    // Getters and Setters
    public String getQueryPrompt() { return queryPrompt; }
    public void setQueryPrompt(String queryPrompt) { this.queryPrompt = queryPrompt; }

    public TaskCategory getClassifiedCategory() { return classifiedCategory; }
    public void setClassifiedCategory(TaskCategory classifiedCategory) { this.classifiedCategory = classifiedCategory; }

    public double getClassificationConfidence() { return classificationConfidence; }
    public void setClassificationConfidence(double classificationConfidence) { this.classificationConfidence = classificationConfidence; }

    public Map<String, Double> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Double> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public TaskRequirements getExtractedRequirements() { return extractedRequirements; }
    public void setExtractedRequirements(TaskRequirements extractedRequirements) { this.extractedRequirements = extractedRequirements; }

    public PriorityWeights getActiveWeights() { return activeWeights; }
    public void setActiveWeights(PriorityWeights activeWeights) { this.activeWeights = activeWeights; }

    public ModelRankDetail getTopModel() { return topModel; }
    public void setTopModel(ModelRankDetail topModel) { this.topModel = topModel; }

    public List<ModelRankDetail> getRankedAlternatives() { return rankedAlternatives; }
    public void setRankedAlternatives(List<ModelRankDetail> rankedAlternatives) { this.rankedAlternatives = rankedAlternatives; }

    public String getWhyRecommended() { return whyRecommended; }
    public void setWhyRecommended(String whyRecommended) { this.whyRecommended = whyRecommended; }

    public String getPotentialDrawbacks() { return potentialDrawbacks; }
    public void setPotentialDrawbacks(String potentialDrawbacks) { this.potentialDrawbacks = potentialDrawbacks; }

    public String getFormulaBreakdown() { return formulaBreakdown; }
    public void setFormulaBreakdown(String formulaBreakdown) { this.formulaBreakdown = formulaBreakdown; }

    public String getGeminiAiInsights() { return geminiAiInsights; }
    public void setGeminiAiInsights(String geminiAiInsights) { this.geminiAiInsights = geminiAiInsights; }

    public boolean isGeminiPowered() { return geminiPowered; }
    public void setGeminiPowered(boolean geminiPowered) { this.geminiPowered = geminiPowered; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getEngineInfo() { return engineInfo; }
    public void setEngineInfo(String engineInfo) { this.engineInfo = engineInfo; }
}
