package com.modelmatch.model;

/**
 * Structured requirements extracted from the user's task description.
 */
public class TaskRequirements {
    private TaskCategory taskCategory;
    private double reasoning;    // 0.0 - 1.0
    private double coding;       // 0.0 - 1.0
    private double context;      // 0.0 - 1.0
    private double multimodal;   // 0.0 - 1.0
    private double speed;        // 0.0 - 1.0
    private double cost;         // 0.0 - 1.0 (higher = more cost-sensitive / prefers cheaper)
    private String outputComplexity; // Low, Medium, High
    private int estimatedInputTokens = 2000;
    private int estimatedOutputTokens = 1000;
    private String keyConstraints;
    private String extractionRationale;

    public TaskRequirements() {}

    public TaskRequirements(TaskCategory taskCategory, double reasoning, double coding,
                            double context, double multimodal, double speed, double cost,
                            String outputComplexity, String keyConstraints, String extractionRationale) {
        this.taskCategory = taskCategory;
        this.reasoning = clamp(reasoning);
        this.coding = clamp(coding);
        this.context = clamp(context);
        this.multimodal = clamp(multimodal);
        this.speed = clamp(speed);
        this.cost = clamp(cost);
        this.outputComplexity = outputComplexity;
        this.keyConstraints = keyConstraints;
        this.extractionRationale = extractionRationale;
    }

    private double clamp(double val) {
        return Math.max(0.0, Math.min(1.0, val));
    }

    /**
     * Converts requirements into normalized feature vector [R, C, X, M, S, P].
     */
    public double[] getFeatureVector() {
        return new double[]{
            reasoning,
            coding,
            context,
            multimodal,
            speed,
            cost
        };
    }

    // Getters and Setters
    public TaskCategory getTaskCategory() { return taskCategory; }
    public void setTaskCategory(TaskCategory taskCategory) { this.taskCategory = taskCategory; }

    public double getReasoning() { return reasoning; }
    public void setReasoning(double reasoning) { this.reasoning = clamp(reasoning); }

    public double getCoding() { return coding; }
    public void setCoding(double coding) { this.coding = clamp(coding); }

    public double getContext() { return context; }
    public void setContext(double context) { this.context = clamp(context); }

    public double getMultimodal() { return multimodal; }
    public void setMultimodal(double multimodal) { this.multimodal = clamp(multimodal); }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = clamp(speed); }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = clamp(cost); }

    public String getOutputComplexity() { return outputComplexity; }
    public void setOutputComplexity(String outputComplexity) { this.outputComplexity = outputComplexity; }

    public int getEstimatedInputTokens() { return estimatedInputTokens; }
    public void setEstimatedInputTokens(int estimatedInputTokens) { this.estimatedInputTokens = estimatedInputTokens; }

    public int getEstimatedOutputTokens() { return estimatedOutputTokens; }
    public void setEstimatedOutputTokens(int estimatedOutputTokens) { this.estimatedOutputTokens = estimatedOutputTokens; }

    public String getKeyConstraints() { return keyConstraints; }
    public void setKeyConstraints(String keyConstraints) { this.keyConstraints = keyConstraints; }

    public String getExtractionRationale() { return extractionRationale; }
    public void setExtractionRationale(String extractionRationale) { this.extractionRationale = extractionRationale; }
}
