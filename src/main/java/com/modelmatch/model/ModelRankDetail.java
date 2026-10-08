package com.modelmatch.model;

import java.util.List;

/**
 * Detailed scoring information for a single candidate model.
 */
public class ModelRankDetail {
    private int rank;
    private ModelEntity model;
    private double finalScore;          // 0.0 - 100.0 (percentage match)
    private double cosineSimilarity;    // 0.0 - 1.0
    private double weightedScore;       // 0.0 - 100.0
    private double estimatedQueryCost;  // in USD for user's estimated prompt/response
    private List<String> matchHighlights;
    private List<String> potentialDrawbacks;
    private String rationale;

    public ModelRankDetail() {}

    public ModelRankDetail(int rank, ModelEntity model, double finalScore,
                           double cosineSimilarity, double weightedScore,
                           double estimatedQueryCost, List<String> matchHighlights,
                           List<String> potentialDrawbacks, String rationale) {
        this.rank = rank;
        this.model = model;
        this.finalScore = finalScore;
        this.cosineSimilarity = cosineSimilarity;
        this.weightedScore = weightedScore;
        this.estimatedQueryCost = estimatedQueryCost;
        this.matchHighlights = matchHighlights;
        this.potentialDrawbacks = potentialDrawbacks;
        this.rationale = rationale;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public ModelEntity getModel() { return model; }
    public void setModel(ModelEntity model) { this.model = model; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }

    public double getCosineSimilarity() { return cosineSimilarity; }
    public void setCosineSimilarity(double cosineSimilarity) { this.cosineSimilarity = cosineSimilarity; }

    public double getWeightedScore() { return weightedScore; }
    public void setWeightedScore(double weightedScore) { this.weightedScore = weightedScore; }

    public double getEstimatedQueryCost() { return estimatedQueryCost; }
    public void setEstimatedQueryCost(double estimatedQueryCost) { this.estimatedQueryCost = estimatedQueryCost; }

    public List<String> getMatchHighlights() { return matchHighlights; }
    public void setMatchHighlights(List<String> matchHighlights) { this.matchHighlights = matchHighlights; }

    public List<String> getPotentialDrawbacks() { return potentialDrawbacks; }
    public void setPotentialDrawbacks(List<String> potentialDrawbacks) { this.potentialDrawbacks = potentialDrawbacks; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
}
