package com.modelmatch.classifier;

import com.modelmatch.model.TaskCategory;
import com.modelmatch.model.TaskRequirements;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Extracts normalized quantitative requirements [0.0 - 1.0] across:
 * - Reasoning
 * - Coding
 * - Context
 * - Multimodal
 * - Speed
 * - Cost Sensitivity
 * From the user's natural language task description.
 */
public class TaskRequirementExtractor {

    public static TaskRequirements extractRequirements(String prompt, TaskCategory category) {
        String lower = prompt != null ? prompt.toLowerCase(Locale.ROOT) : "";

        // Default baseline vectors depending on the classified category
        double reasoning = 0.50;
        double coding = 0.20;
        double context = 0.40;
        double multimodal = 0.15;
        double speed = 0.60;
        double cost = 0.50;
        String complexity = "Medium";
        int inputTokens = 2500;
        int outputTokens = 1200;

        switch (category) {
            case CODING:
                coding = 0.92;
                reasoning = 0.85;
                context = 0.70;
                complexity = "High";
                outputTokens = 2000;
                break;
            case REASONING:
                reasoning = 0.98;
                coding = 0.20;
                context = 0.25;
                multimodal = 0.05;
                speed = 0.30;
                cost = 0.25;
                complexity = "High";
                outputTokens = 1500;
                break;
            case RESEARCH:
                context = 0.92;
                reasoning = 0.78;
                multimodal = 0.35;
                inputTokens = 15000;
                outputTokens = 2500;
                break;
            case MULTIMODAL:
                multimodal = 0.95;
                reasoning = 0.70;
                break;
            case WRITING:
                reasoning = 0.65;
                coding = 0.10;
                context = 0.50;
                outputTokens = 2500;
                break;
            case TRANSLATION:
                speed = 0.75;
                cost = 0.70;
                break;
            case AGENTIC:
                reasoning = 0.92;
                coding = 0.75;
                speed = 0.80;
                context = 0.75;
                complexity = "High";
                break;
            case GENERAL:
            default:
                break;
        }

        // Adjust based on explicit keywords in prompt:
        
        // 1. REASONING
        if (containsAny(lower, "complex reasoning", "deep reasoning", "hard logic", "mathematical proof", "deep thinking", "heavy reasoning")) {
            reasoning = Math.max(reasoning, 0.92);
        } else if (containsAny(lower, "reasoning", "logic", "analyze", "critical thinking", "deduction")) {
            reasoning = Math.max(reasoning, 0.80);
        } else if (containsAny(lower, "simple", "basic", "quick question", "trivial")) {
            reasoning = Math.min(reasoning, 0.40);
        }

        // 2. CODING
        if (containsAny(lower, "lots of code", "full stack", "production-ready", "rest api", "backend", "microservice", "refactor", "software development", "debugging")) {
            coding = Math.max(coding, 0.95);
        } else if (containsAny(lower, "code", "programming", "python", "java", "script", "algorithm", "sql", "bug", "frontend", "git")) {
            coding = Math.max(coding, 0.85);
        } else if (containsAny(lower, "no code", "non-technical", "pure text")) {
            coding = 0.05;
        }

        // 3. CONTEXT
        if (containsAny(lower, "large context", "huge context", "massive context", "entire codebase", "100-page", "long document", "multi-page", "book", "entire repo")) {
            context = Math.max(context, 0.88);
            inputTokens = 35000;
        } else if (containsAny(lower, "context window", "long context", "summarize paper", "literature review", "pdf")) {
            context = Math.max(context, 0.75);
            inputTokens = 12000;
        } else if (containsAny(lower, "short", "snippet", "brief", "one sentence")) {
            context = Math.min(context, 0.30);
            inputTokens = 500;
        }

        // 4. MULTIMODAL
        if (containsAny(lower, "multimodal", "image", "vision", "screenshot", "chart", "diagram", "ocr", "graph", "photo", "pdf chart")) {
            multimodal = Math.max(multimodal, 0.90);
        } else if (containsAny(lower, "visual", "picture", "figure")) {
            multimodal = Math.max(multimodal, 0.75);
        } else if (containsAny(lower, "text only", "no images", "low multimodal")) {
            multimodal = 0.10;
        }

        // 5. SPEED
        if (containsAny(lower, "ultra fast", "real-time", "instant", "sub-second", "low latency", "high throughput", "streaming")) {
            speed = Math.max(speed, 0.95);
        } else if (containsAny(lower, "fast", "speed priority", "quick", "responsive")) {
            speed = Math.max(speed, 0.80);
        } else if (containsAny(lower, "take your time", "offline batch", "slow is fine", "background job")) {
            speed = 0.35;
        }

        // 6. COST SENSITIVITY
        if (containsAny(lower, "limited budget", "cheap", "cost-effective", "free tier", "tight budget", "low cost", "shoestring", "inexpensive", "minimize cost")) {
            cost = Math.max(cost, 0.92);
        } else if (containsAny(lower, "cost", "budget", "affordable", "price sensitive")) {
            cost = Math.max(cost, 0.75);
        } else if (containsAny(lower, "money is no object", "enterprise budget", "cost doesn't matter", "budget is not an issue", "budget is not a concern", "highest quality at any price")) {
            cost = 0.20;
        }

        // Output complexity
        if (reasoning > 0.85 || coding > 0.85 || containsAny(lower, "production", "enterprise", "complex", "architecture")) {
            complexity = "High";
        } else if (containsAny(lower, "simple", "easy", "casual", "quick")) {
            complexity = "Low";
        }

        String constraints = extractConstraints(lower);
        String rationale = String.format(
            "Identified as %s task. Reasoning requirement: %.2f, Coding: %.2f, Context: %.2f, Multimodal: %.2f, Speed: %.2f, Cost sensitivity: %.2f.",
            category.getDisplayName(), reasoning, coding, context, multimodal, speed, cost
        );

        TaskRequirements req = new TaskRequirements(
            category, reasoning, coding, context, multimodal, speed, cost, complexity, constraints, rationale
        );
        req.setEstimatedInputTokens(inputTokens);
        req.setEstimatedOutputTokens(outputTokens);
        return req;
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private static String extractConstraints(String lower) {
        StringBuilder sb = new StringBuilder();
        if (lower.contains("budget") || lower.contains("cost")) sb.append("Strict Budget Limit; ");
        if (lower.contains("context") || lower.contains("large")) sb.append("High Context Retention; ");
        if (lower.contains("fast") || lower.contains("latency")) sb.append("Low Latency Required; ");
        if (lower.contains("privacy") || lower.contains("local") || lower.contains("open source")) sb.append("Open Weights / Privacy; ");
        if (sb.length() == 0) return "Standard Requirements";
        return sb.toString().trim();
    }
}
