package com.modelmatch.model;

/**
 * User priority weights for the recommendation formula:
 * Score = w_r*R + w_c*C + w_x*X + w_m*M + w_s*S + w_p*P
 */
public class PriorityWeights {
    private double weightReasoning = 0.25;
    private double weightCoding = 0.25;
    private double weightContext = 0.15;
    private double weightMultimodal = 0.10;
    private double weightSpeed = 0.10;
    private double weightCost = 0.15;

    public PriorityWeights() {}

    public PriorityWeights(double weightReasoning, double weightCoding, double weightContext,
                           double weightMultimodal, double weightSpeed, double weightCost) {
        this.weightReasoning = weightReasoning;
        this.weightCoding = weightCoding;
        this.weightContext = weightContext;
        this.weightMultimodal = weightMultimodal;
        this.weightSpeed = weightSpeed;
        this.weightCost = weightCost;
        normalize();
    }

    /**
     * Normalizes weights so their sum equals 1.0.
     */
    public void normalize() {
        double sum = weightReasoning + weightCoding + weightContext + weightMultimodal + weightSpeed + weightCost;
        if (sum > 0.0001) {
            weightReasoning /= sum;
            weightCoding /= sum;
            weightContext /= sum;
            weightMultimodal /= sum;
            weightSpeed /= sum;
            weightCost /= sum;
        } else {
            // Default equal weights
            weightReasoning = 1.0 / 6.0;
            weightCoding = 1.0 / 6.0;
            weightContext = 1.0 / 6.0;
            weightMultimodal = 1.0 / 6.0;
            weightSpeed = 1.0 / 6.0;
            weightCost = 1.0 / 6.0;
        }
    }

    /**
     * Derives default adaptive weights from task requirements.
     */
    public static PriorityWeights fromRequirements(TaskRequirements req) {
        double r = Math.max(0.05, req.getReasoning());
        double c = Math.max(0.05, req.getCoding());
        double x = Math.max(0.05, req.getContext());
        double m = Math.max(0.05, req.getMultimodal());
        double s = Math.max(0.05, req.getSpeed());
        double p = Math.max(0.05, req.getCost());

        PriorityWeights weights = new PriorityWeights(r, c, x, m, s, p);
        weights.normalize();
        return weights;
    }

    public double getWeightReasoning() { return weightReasoning; }
    public void setWeightReasoning(double weightReasoning) { this.weightReasoning = weightReasoning; }

    public double getWeightCoding() { return weightCoding; }
    public void setWeightCoding(double weightCoding) { this.weightCoding = weightCoding; }

    public double getWeightContext() { return weightContext; }
    public void setWeightContext(double weightContext) { this.weightContext = weightContext; }

    public double getWeightMultimodal() { return weightMultimodal; }
    public void setWeightMultimodal(double weightMultimodal) { this.weightMultimodal = weightMultimodal; }

    public double getWeightSpeed() { return speed(); }
    public double speed() { return weightSpeed; }
    public void setWeightSpeed(double weightSpeed) { this.weightSpeed = weightSpeed; }

    public double getWeightCost() { return weightCost; }
    public void setWeightCost(double weightCost) { this.weightCost = weightCost; }
}
