package com.modelmatch.model;

/**
 * Task Categories for classification of user requests.
 */
public enum TaskCategory {
    CODING("Coding", "Debugging, software development, code generation, algorithms, refactoring, API integration"),
    REASONING("Reasoning", "Mathematics, logic proofs, formal reasoning, scientific problem solving, data deduction"),
    WRITING("Writing", "Articles, blog posts, essays, creative writing, copywriting, emails, storytelling"),
    RESEARCH("Research", "Information retrieval, long document summarization, academic literature review, synthesis"),
    MULTIMODAL("Multimodal", "Vision analysis, charts/diagrams, document OCR, image inspection, video comprehension"),
    TRANSLATION("Translation", "Language translation, localization, cross-lingual adaptation, linguistic nuances"),
    GENERAL("General", "Everyday queries, chit-chat, brainstorming, general knowledge, open Q&A"),
    AGENTIC("Agentic", "Tool use, function calling, multi-step autonomous planning, web navigation, workflow execution");

    private final String displayName;
    private final String description;

    TaskCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static TaskCategory fromString(String text) {
        if (text == null) return GENERAL;
        for (TaskCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(text.trim()) || cat.displayName.equalsIgnoreCase(text.trim())) {
                return cat;
            }
        }
        return GENERAL;
    }
}
