package com.modelmatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an LLM, its capabilities, benchmark scores,
 * context specifications, cost parameters, and execution capabilities.
 *
 * Feature vector order: [Reasoning, Coding, Context, Multimodal, Speed, CostEfficiency]
 */
public class ModelEntity {
    private String id;
    private String name;
    private String provider;
    private String badge;
    private double reasoningScore;   // 0 - 100
    private double codingScore;      // 0 - 100
    private double contextScore;     // 0 - 100
    private double multimodalScore;  // 0 - 100
    private double speedScore;       // 0 - 100
    private double costScore;        // 0 - 100 (higher = cheaper/more cost-effective)
    private double overallScore;     // 0 - 100 benchmark aggregate
    
    // Technical specs
    private int contextWindowTokens;
    private double inputCostPer1M;   // USD per 1M tokens
    private double outputCostPer1M;  // USD per 1M tokens
    private int speedTokensPerSec;   // Average generation speed (tok/s)
    private String license;          // Proprietary vs Open Weights
    private String benchmarkSource;  // Verified source: SWE-bench, LiveCodeBench, MMLU-Pro, MATH-500
    private String description;
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();

    // Model Provider Execution & Citation Architecture (Phases 3, 4, 7)
    private boolean directExecutionSupported; // true if ModelMatch has live API execution integration (e.g. Gemini)
    private String sourceReference;           // Documentation / benchmark citation URL or reference

    public ModelEntity() {}

    public ModelEntity(String id, String name, String provider, String badge,
                       double reasoningScore, double codingScore, double contextScore,
                       double multimodalScore, double speedScore, double costScore,
                       double overallScore, int contextWindowTokens, double inputCostPer1M,
                       double outputCostPer1M, int speedTokensPerSec, String license,
                       String benchmarkSource, String description,
                       List<String> strengths, List<String> weaknesses) {
        this(id, name, provider, badge, reasoningScore, codingScore, contextScore,
             multimodalScore, speedScore, costScore, overallScore, contextWindowTokens,
             inputCostPer1M, outputCostPer1M, speedTokensPerSec, license,
             benchmarkSource, description, strengths, weaknesses, false, "");
    }

    public ModelEntity(String id, String name, String provider, String badge,
                       double reasoningScore, double codingScore, double contextScore,
                       double multimodalScore, double speedScore, double costScore,
                       double overallScore, int contextWindowTokens, double inputCostPer1M,
                       double outputCostPer1M, int speedTokensPerSec, String license,
                       String benchmarkSource, String description,
                       List<String> strengths, List<String> weaknesses,
                       boolean directExecutionSupported, String sourceReference) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.badge = badge;
        this.reasoningScore = reasoningScore;
        this.codingScore = codingScore;
        this.contextScore = contextScore;
        this.multimodalScore = multimodalScore;
        this.speedScore = speedScore;
        this.costScore = costScore;
        this.overallScore = overallScore;
        this.contextWindowTokens = contextWindowTokens;
        this.inputCostPer1M = inputCostPer1M;
        this.outputCostPer1M = outputCostPer1M;
        this.speedTokensPerSec = speedTokensPerSec;
        this.license = license;
        this.benchmarkSource = benchmarkSource;
        this.description = description;
        this.strengths = strengths != null ? strengths : new ArrayList<>();
        this.weaknesses = weaknesses != null ? weaknesses : new ArrayList<>();
        this.directExecutionSupported = directExecutionSupported;
        this.sourceReference = sourceReference;
    }

    /**
     * Converts model capability scores into a normalized 6D feature vector [0.0 - 1.0].
     * Vector order: [Reasoning, Coding, Context, Multimodal, Speed, CostEfficiency]
     */
    public double[] getFeatureVector() {
        return new double[]{
            reasoningScore / 100.0,
            codingScore / 100.0,
            contextScore / 100.0,
            multimodalScore / 100.0,
            speedScore / 100.0,
            costScore / 100.0
        };
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public double getReasoningScore() { return reasoningScore; }
    public void setReasoningScore(double reasoningScore) { this.reasoningScore = reasoningScore; }

    public double getCodingScore() { return codingScore; }
    public void setCodingScore(double codingScore) { this.codingScore = codingScore; }

    public double getContextScore() { return contextScore; }
    public void setContextScore(double contextScore) { this.contextScore = contextScore; }

    public double getMultimodalScore() { return multimodalScore; }
    public void setMultimodalScore(double multimodalScore) { this.multimodalScore = multimodalScore; }

    public double getSpeedScore() { return speedScore; }
    public void setSpeedScore(double speedScore) { this.speedScore = speedScore; }

    public double getCostScore() { return costScore; }
    public void setCostScore(double costScore) { this.costScore = costScore; }

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }

    public int getContextWindowTokens() { return contextWindowTokens; }
    public void setContextWindowTokens(int contextWindowTokens) { this.contextWindowTokens = contextWindowTokens; }

    public double getInputCostPer1M() { return inputCostPer1M; }
    public void setInputCostPer1M(double inputCostPer1M) { this.inputCostPer1M = inputCostPer1M; }

    public double getOutputCostPer1M() { return outputCostPer1M; }
    public void setOutputCostPer1M(double outputCostPer1M) { this.outputCostPer1M = outputCostPer1M; }

    public int getSpeedTokensPerSec() { return speedTokensPerSec; }
    public void setSpeedTokensPerSec(int speedTokensPerSec) { this.speedTokensPerSec = speedTokensPerSec; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public String getBenchmarkSource() { return benchmarkSource; }
    public void setBenchmarkSource(String benchmarkSource) { this.benchmarkSource = benchmarkSource; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public boolean isDirectExecutionSupported() { return directExecutionSupported; }
    public void setDirectExecutionSupported(boolean directExecutionSupported) { this.directExecutionSupported = directExecutionSupported; }

    public String getSourceReference() { return sourceReference; }
    public void setSourceReference(String sourceReference) { this.sourceReference = sourceReference; }
}
