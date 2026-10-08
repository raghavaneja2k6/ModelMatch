package com.modelmatch.repository;

import com.modelmatch.model.ModelEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Model repository containing leading Large Language Models,
 * their capability vectors, transparent benchmark citations,
 * context windows, and real-world pricing.
 */
public class ModelDatabase {

    private final Map<String, ModelEntity> models = new ConcurrentHashMap<>();

    public ModelDatabase() {
        seedDatabase();
    }

    private void seedDatabase() {
        // 1. Claude 3.7 Sonnet (Anthropic)
        register(new ModelEntity(
            "claude-3-7-sonnet",
            "Claude 3.7 Sonnet",
            "Anthropic",
            "State of the Art (Hybrid)",
            96.0, 97.0, 92.0, 93.0, 74.0, 60.0, 94.0,
            200000, 3.00, 15.00, 75, "Proprietary",
            "SWE-bench Verified (70.3%), LiveCodeBench (65.9%), MATH-500",
            "Top-tier hybrid reasoning and software engineering model. Features dual standard/extended thinking modes.",
            Arrays.asList("Industry-leading coding & refactoring", "Complex architectural reasoning", "Excellent instruction adherence"),
            Arrays.asList("Premium pricing per 1M output tokens", "Moderate latency in extended thinking mode")
        ));

        // 2. GPT-4o (OpenAI)
        register(new ModelEntity(
            "gpt-4o",
            "GPT-4o",
            "OpenAI",
            "Omni Flagship",
            92.0, 90.0, 88.0, 96.0, 88.0, 65.0, 91.0,
            128000, 2.50, 10.00, 110, "Proprietary",
            "LMSYS Chatbot Arena Elo (1335+), MMLU-Pro (72.6%)",
            "Native omni-modal flagship model with strong visual reasoning, high throughput, and broad versatility.",
            Arrays.asList("Exceptional vision and chart comprehension", "Fast streaming responses", "Highly reliable JSON mode"),
            Arrays.asList("Context capped at 128k", "Output cost can add up for large generations")
        ));

        // 3. Gemini 3.5 Flash (Google)
        register(new ModelEntity(
            "gemini-3-5-flash",
            "Gemini 3.5 Flash",
            "Google",
            "Speed & 1M Context Leader",
            88.0, 87.0, 98.0, 92.0, 96.0, 95.0, 91.0,
            1048576, 0.075, 0.30, 150, "Proprietary (Generative Language API)",
            "Needle In A Haystack 1M (99.8%), MMLU (84.1%), LMSYS Arena",
            "Ultra-fast, cost-effective multimodal model with a massive 1-million-token context window and free tier access.",
            Arrays.asList("Massive 1M token context capacity", "Extremely low cost with generous free tier", "Blazing fast generation speed"),
            Arrays.asList("Slightly behind Sonnet 3.7 on deeply intricate coding puzzles")
        ));

        // 4. DeepSeek R1 (DeepSeek)
        register(new ModelEntity(
            "deepseek-r1",
            "DeepSeek R1",
            "DeepSeek",
            "Open Reasoning Powerhouse",
            97.0, 93.0, 86.0, 20.0, 60.0, 92.0, 92.0,
            128000, 0.55, 2.19, 55, "Open Weights (MIT)",
            "MATH-500 (97.3%), AIME 2024 (79.8%), Codeforces 96.3 percentile",
            "Open-weights reinforcement-learning reasoning model rivaling OpenAI o1 at a fraction of the inference cost.",
            Arrays.asList("Extraordinary mathematical logic and step-by-step proofs", "Unbeatable reasoning-to-cost ratio", "Open weights transparency"),
            Arrays.asList("No native vision/multimodal capabilities", "Verbose reasoning traces increase token volume")
        ));

        // 5. Qwen 2.5 Coder 32B (Alibaba)
        register(new ModelEntity(
            "qwen-2-5-coder-32b",
            "Qwen 2.5 Coder 32B",
            "Alibaba Cloud",
            "Open Source Code Champion",
            86.0, 95.0, 88.0, 15.0, 84.0, 93.0, 88.0,
            131072, 0.20, 0.60, 95, "Open Weights (Apache 2.0)",
            "EvalPlus (88.4), HumanEval (92.7%), MultiPL-E benchmark",
            "Dedicated code generation model outperforming many larger proprietary models on multi-language programming tasks.",
            Arrays.asList("Outstanding multi-language coding synthesis", "Can be self-hosted privately", "Highly economical API pricing"),
            Arrays.asList("Text and code only (no image support)", "Narrower general knowledge compared to general LLMs")
        ));

        // 6. Gemini 2.5 Pro (Google)
        register(new ModelEntity(
            "gemini-2-5-pro",
            "Gemini 2.5 Pro",
            "Google",
            "2M Context Titan",
            94.0, 92.0, 100.0, 95.0, 68.0, 58.0, 93.0,
            2097152, 1.25, 5.00, 65, "Proprietary",
            "Needle In A Haystack 2M (99.7%), GPQA Diamond (58.5%), Math-500",
            "Deep reasoning model engineered for massive multi-document analysis, full codebase ingestion, and complex multimodal tasks.",
            Arrays.asList("Industry-record 2 Million token context window", "Superb video, audio, and visual reasoning", "Deep analytical research capability"),
            Arrays.asList("Higher latency than Flash tier", "Requires thoughtful prompt structuring")
        ));

        // 7. Meta Llama 3.3 70B (Meta)
        register(new ModelEntity(
            "llama-3-3-70b",
            "Llama 3.3 70B",
            "Meta",
            "Open Enterprise Standard",
            86.0, 85.0, 86.0, 20.0, 86.0, 92.0, 86.0,
            128000, 0.18, 0.60, 120, "Open Weights (Llama 3.3 Community)",
            "MMLU (88.6%), GSM8K (93.1%), Chatbot Arena Elo (1280+)",
            "The industry benchmark for open-weights enterprise deployment, offering GPT-4 class general intelligence.",
            Arrays.asList("Full data privacy via on-premise deployment", "High inference speed on Groq/Cerebras", "Strong general reasoning"),
            Arrays.asList("No vision capability", "Limited context window (128k) compared to Gemini")
        ));

        // 8. OpenAI o3-mini (OpenAI)
        register(new ModelEntity(
            "o3-mini",
            "OpenAI o3-mini",
            "OpenAI",
            "STEM & Coding Specialist",
            95.0, 94.0, 88.0, 10.0, 78.0, 82.0, 90.0,
            200000, 1.10, 4.40, 80, "Proprietary",
            "Codeforces 2083 rating, AIME 2024 (87.3%), SWE-bench",
            "Compact reasoning model specialized in STEM, competitive programming, and multi-step algorithm formulation.",
            Arrays.asList("Top-tier competitive programming performance", "Configurable reasoning effort (low/med/high)", "Generous 200k context"),
            Arrays.asList("Text-only (multimodal input not supported)", "Inconsistent performance on creative writing")
        ));

        // 9. GPT-4o-mini (OpenAI)
        register(new ModelEntity(
            "gpt-4o-mini",
            "GPT-4o-mini",
            "OpenAI",
            "Affordable Everyday Assistant",
            79.0, 80.0, 86.0, 84.0, 94.0, 94.0, 82.0,
            128000, 0.15, 0.60, 130, "Proprietary",
            "MMLU (82.0%), HumanEval (87.2%), LMSYS Arena",
            "Cost-effective lightweight omni model designed for high-volume tasks, quick lookups, and budget-sensitive workflows.",
            Arrays.asList("Extremely cost-effective", "Low latency and high rate limits", "Good vision capabilities for price"),
            Arrays.asList("Lower reasoning depth on intricate logic", "Can fail on difficult edge-case coding")
        ));

        // 10. Claude 3.5 Haiku (Anthropic)
        register(new ModelEntity(
            "claude-3-5-haiku",
            "Claude 3.5 Haiku",
            "Anthropic",
            "Rapid Execution Champion",
            83.0, 84.0, 88.0, 82.0, 96.0, 85.0, 85.0,
            200000, 0.80, 4.00, 140, "Proprietary",
            "SWE-bench Verified (40.6%), LiveCodeBench (42.5%)",
            "Ultra-responsive model combining near-instant time-to-first-token with solid coding and reasoning skills.",
            Arrays.asList("Sub-second response latency", "200k context window", "Exceptional tool-calling speed for agents"),
            Arrays.asList("Higher cost than Gemini Flash or GPT-4o-mini", "Less comprehensive factual recall")
        ));

        // 11. Mistral Large 2 (Mistral AI)
        register(new ModelEntity(
            "mistral-large-2",
            "Mistral Large 2",
            "Mistral AI",
            "Multilingual & Enterprise",
            89.0, 88.0, 86.0, 30.0, 76.0, 72.0, 87.0,
            128000, 2.00, 6.00, 80, "Proprietary & Open Weights",
            "MMLU (84.0%), HumanEval (92.0%), Multilingual Benchmarks",
            "Advanced 123B model with native multilingual fluency across French, German, Spanish, Italian, and strong code reasoning.",
            Arrays.asList("Premier European multilingual performance", "Precise JSON and function calling", "Strong mathematical foundation"),
            Arrays.asList("Limited visual processing", "Context limit at 128k")
        ));

        // 12. DeepSeek V3 (DeepSeek)
        register(new ModelEntity(
            "deepseek-v3",
            "DeepSeek V3",
            "DeepSeek",
            "Frontier MoE Value",
            91.0, 92.0, 86.0, 20.0, 86.0, 96.0, 90.0,
            128000, 0.14, 0.28, 90, "Open Weights (MIT)",
            "MMLU (88.5%), HumanEval (82.6%), Arena Elo (1310+)",
            "State of the art 671B Mixture-of-Experts architecture delivering frontier proprietary intelligence at rock-bottom cost.",
            Arrays.asList("Unprecedented cost-efficiency ($0.14 / 1M tokens)", "High coding and reasoning benchmarks", "Fast inference throughput"),
            Arrays.asList("Text-only input", "Hosted API availability can experience peak demand queues")
        ));
    }

    public void register(ModelEntity model) {
        models.put(model.getId(), model);
    }

    public List<ModelEntity> getAllModels() {
        return new ArrayList<>(models.values());
    }

    public Optional<ModelEntity> getModelById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(models.get(id.toLowerCase().trim()));
    }

    public List<ModelEntity> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllModels();
        }
        String q = query.toLowerCase();
        return models.values().stream()
            .filter(m -> m.getName().toLowerCase().contains(q)
                || m.getProvider().toLowerCase().contains(q)
                || m.getDescription().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }
}
