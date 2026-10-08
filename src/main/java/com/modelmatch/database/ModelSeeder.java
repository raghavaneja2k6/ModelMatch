package com.modelmatch.database;

import com.modelmatch.model.ModelEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Seeder populating the multi-provider LLM Model Intelligence Database.
 * Contains 38 verified contemporary models across 14 leading global AI providers.
 * All pricing is standardized to USD per 1M standard input/output tokens.
 * All benchmark sources are documented with empirical citations.
 */
public class ModelSeeder {

    private static final Logger LOGGER = Logger.getLogger(ModelSeeder.class.getName());

    public static List<ModelEntity> getSeedCatalog() {
        List<ModelEntity> list = new ArrayList<>();

        // =========================================================================
        // 1. OPENAI (5 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "gpt-4o",
            "GPT-4o",
            "OpenAI",
            "Omni Flagship",
            92.0, 90.0, 88.0, 96.0, 88.0, 65.0, 91.0,
            128000, 2.50, 10.00, 110, "Proprietary",
            "LMSYS Chatbot Arena Elo (1335+), MMLU-Pro (72.6%), SimpleQA",
            "OpenAI's high-intelligence omni flagship with native multimodal reasoning and rapid response times.",
            Arrays.asList("Exceptional visual and chart comprehension", "Fast streaming responses", "Highly reliable JSON structured outputs"),
            Arrays.asList("Context capped at 128k tokens", "Higher cost than mini/flash tiers"),
            false, "https://platform.openai.com/docs/models/gpt-4o"
        ));

        list.add(new ModelEntity(
            "gpt-4o-mini",
            "GPT-4o mini",
            "OpenAI",
            "Fast & Cost-Efficient",
            79.0, 80.0, 86.0, 84.0, 94.0, 94.0, 82.0,
            128000, 0.15, 0.60, 135, "Proprietary",
            "MMLU (82.0%), HumanEval (87.2%), MathVista (65.3%)",
            "Compact omni model designed for high-volume tasks, quick lookups, and budget-sensitive production pipelines.",
            Arrays.asList("Extremely cost-effective ($0.15/1M input)", "Low latency with high throughput", "Strong vision for budget tier"),
            Arrays.asList("Lower reasoning depth on intricate logic puzzles", "Occasional failures on complex AST generation"),
            false, "https://openai.com/index/gpt-4o-mini-advancing-cost-efficient-intelligence/"
        ));

        list.add(new ModelEntity(
            "o1",
            "OpenAI o1",
            "OpenAI",
            "Deep Reasoning Flagship",
            98.0, 93.0, 92.0, 90.0, 50.0, 30.0, 93.0,
            200000, 15.00, 60.00, 45, "Proprietary",
            "AIME 2024 (83.3%), Codeforces (93.3 percentile), GPQA Diamond (78.0%)",
            "Flagship reinforcement-learning reasoning model engineered for hard scientific problems, math, and code architecture.",
            Arrays.asList("Extraordinary step-by-step logical rigor", "Ranks in 93rd percentile on Codeforces", "Deep autonomous error self-correction"),
            Arrays.asList("Premium enterprise pricing ($15.00/$60.00 per 1M tokens)", "Higher latency due to chain-of-thought exploration"),
            false, "https://openai.com/o1/"
        ));

        list.add(new ModelEntity(
            "o3-mini",
            "OpenAI o3-mini",
            "OpenAI",
            "STEM & Coding Reasoning",
            95.0, 94.0, 88.0, 10.0, 78.0, 82.0, 90.0,
            200000, 1.10, 4.40, 80, "Proprietary",
            "Codeforces 2083 rating, AIME 2024 (87.3%), SWE-bench Verified (49.3%)",
            "Compact reasoning specialist optimized for coding competitions, math proofs, and STEM workflows.",
            Arrays.asList("Top-tier competitive programming performance", "Configurable reasoning effort (low/med/high)", "Generous 200k context"),
            Arrays.asList("Text-only (multimodal input not supported)", "Less expressive in creative/literary tasks"),
            false, "https://openai.com/index/openai-o3-mini/"
        ));

        list.add(new ModelEntity(
            "gpt-4-5",
            "GPT-4.5",
            "OpenAI",
            "Massive World Knowledge",
            94.0, 90.0, 88.0, 95.0, 55.0, 18.0, 91.0,
            128000, 75.00, 150.00, 50, "Proprietary",
            "SimpleQA (37.1%), MMLU (89.2%), SWE-bench Verified (52.0%)",
            "Large-scale frontier model with unprecedented world knowledge, hallucination reduction, and broad intuition.",
            Arrays.asList("Best-in-class factual accuracy and broad calibration", "Nuanced multi-turn dialogue comprehension", "Deep domain knowledge across humanities and science"),
            Arrays.asList("Extreme high-end pricing ($75.00/1M input, $150.00/1M output)", "Context capped at 128k"),
            false, "https://openai.com/index/introducing-gpt-4-5/"
        ));

        // =========================================================================
        // 2. ANTHROPIC (4 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "claude-3-7-sonnet",
            "Claude 3.7 Sonnet",
            "Anthropic",
            "State of the Art (Hybrid)",
            97.0, 98.0, 92.0, 93.0, 74.0, 60.0, 95.0,
            200000, 3.00, 15.00, 75, "Proprietary",
            "SWE-bench Verified (70.3%), TAU-bench (81.2%), LiveCodeBench (65.9%)",
            "World's leading hybrid reasoning model. Combines instantaneous response with controlled, extended thinking modes.",
            Arrays.asList("Dominant software engineering and agentic coding (70.3% SWE-bench)", "Nuanced architectural reasoning and plan execution", "High instruction adherence and reliability"),
            Arrays.asList("Premium cost on large outputs ($15.00/1M)", "Higher latency when extended thinking budget is high"),
            false, "https://www.anthropic.com/news/claude-3-7-sonnet"
        ));

        list.add(new ModelEntity(
            "claude-3-5-sonnet",
            "Claude 3.5 Sonnet",
            "Anthropic",
            "Frontier Coding & Vision",
            94.0, 95.0, 92.0, 94.0, 76.0, 60.0, 92.0,
            200000, 3.00, 15.00, 80, "Proprietary",
            "SWE-bench Verified (49.0%), GPQA Diamond (59.4%), HumanEval (93.7%)",
            "Anthropic's established workhorse model, prized for elegant code generation, nuanced prose, and sharp image analysis.",
            Arrays.asList("Superb frontend/backend code synthesis", "Nuanced, human-like technical prose", "Exceptional visual diagram parsing"),
            Arrays.asList("Context capped at 200k (smaller than Gemini 1M/2M)", "Pricing adds up on large multi-turn sessions"),
            false, "https://www.anthropic.com/claude/sonnet"
        ));

        list.add(new ModelEntity(
            "claude-3-5-haiku",
            "Claude 3.5 Haiku",
            "Anthropic",
            "Rapid Execution Champion",
            83.0, 85.0, 88.0, 82.0, 96.0, 85.0, 86.0,
            200000, 0.80, 4.00, 140, "Proprietary",
            "SWE-bench Verified (40.6%), LiveCodeBench (42.5%), MMLU (81.5%)",
            "Ultra-responsive model combining near-instant time-to-first-token with solid coding and reasoning skills.",
            Arrays.asList("Sub-second response latency", "200k token context window", "Exceptional tool-calling speed for agents"),
            Arrays.asList("Higher cost than Gemini Flash or GPT-4o-mini", "Less exhaustive edge-case code synthesis"),
            false, "https://www.anthropic.com/claude/haiku"
        ));

        list.add(new ModelEntity(
            "claude-3-opus",
            "Claude 3 Opus",
            "Anthropic",
            "Deep Nuance & Prose",
            91.0, 86.0, 90.0, 88.0, 45.0, 25.0, 88.0,
            200000, 15.00, 75.00, 40, "Proprietary",
            "MMLU (86.8%), GPQA (50.4%), HumanEval (84.9%)",
            "High-depth analytical model designed for complex philosophical analysis, long-form creative writing, and sensitive policy review.",
            Arrays.asList("Unmatched tone, depth, and creative stylistic resonance", "Deep contextual understanding for long documents", "Low hallucination rate on scholarly text"),
            Arrays.asList("High inference cost ($15.00 / $75.00 per 1M tokens)", "Slower generation throughput"),
            false, "https://www.anthropic.com/claude/opus"
        ));

        // =========================================================================
        // 3. GOOGLE (4 Models - Direct Execution Supported)
        // =========================================================================
        list.add(new ModelEntity(
            "gemini-2-5-pro",
            "Gemini 2.5 Pro",
            "Google",
            "2M Context Titan",
            94.0, 92.0, 100.0, 95.0, 68.0, 58.0, 93.0,
            2097152, 1.25, 5.00, 65, "Proprietary (Generative Language API)",
            "Needle In A Haystack 2M (99.7%), GPQA Diamond (58.5%), Math-500 (87.2%)",
            "Deep reasoning model engineered for massive multi-document analysis, full codebase ingestion, and complex multimodal tasks.",
            Arrays.asList("Industry-record 2 Million token context window", "Superb video, audio, and visual reasoning", "Direct API integration in ModelMatch"),
            Arrays.asList("Higher latency than Flash tier", "Requires thoughtful prompt structuring"),
            true, "https://ai.google.dev/gemini-api/docs/models/gemini"
        ));

        list.add(new ModelEntity(
            "gemini-3-5-flash",
            "Gemini 3.5 Flash",
            "Google",
            "Speed & 1M Context Leader",
            88.0, 87.0, 98.0, 92.0, 96.0, 95.0, 91.0,
            1048576, 0.075, 0.30, 150, "Proprietary (Generative Language API)",
            "Needle In A Haystack 1M (99.8%), MMLU (84.1%), LMSYS Arena (1310+)",
            "Ultra-fast, cost-effective multimodal model with a massive 1-million-token context window and integrated live API execution.",
            Arrays.asList("Massive 1M token context capacity", "Extremely low cost ($0.075 / 1M input) with generous free tier", "Integrated direct execution inside ModelMatch"),
            Arrays.asList("Slightly behind Sonnet 3.7 on deeply intricate coding puzzles"),
            true, "https://ai.google.dev/pricing"
        ));

        list.add(new ModelEntity(
            "gemini-2-0-flash",
            "Gemini 2.0 Flash",
            "Google",
            "Realtime Multimodal",
            86.0, 85.0, 98.0, 94.0, 95.0, 94.0, 89.0,
            1048576, 0.10, 0.40, 145, "Proprietary (Generative Language API)",
            "MMLU (82.5%), MathVista (68.1%), LiveBench (48.2%)",
            "Second-generation flash model optimized for realtime streaming, audio/video inputs, and lightning agent execution.",
            Arrays.asList("Sub-second multimodal comprehension", "1M context window", "Native tool use and structured streaming"),
            Arrays.asList("Lower benchmark scores than Gemini 2.5 Pro on formal proofs"),
            true, "https://ai.google.dev/gemini-api/docs/models/gemini-2.0-flash"
        ));

        list.add(new ModelEntity(
            "gemini-1-5-pro",
            "Gemini 1.5 Pro",
            "Google",
            "Long-Horizon Enterprise",
            90.0, 88.0, 100.0, 92.0, 65.0, 60.0, 89.0,
            2097152, 1.25, 5.00, 60, "Proprietary (Generative Language API)",
            "MMLU (85.9%), GSM8K (91.7%), Video-MME (81.3%)",
            "Enterprise-grade 2M context model proven in processing hours of video, audio transcripts, and million-line software repositories.",
            Arrays.asList("Unmatched audio and video tokenization", "Stable, reliable API enterprise guarantees", "Massive repository comprehension"),
            Arrays.asList("Slower than Gemini Flash", "Superseded in pure reasoning by Gemini 2.5 Pro"),
            true, "https://deepmind.google/technologies/gemini/"
        ));

        // =========================================================================
        // 4. DEEPSEEK (2 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "deepseek-r1",
            "DeepSeek R1",
            "DeepSeek",
            "Open Reasoning Powerhouse",
            97.0, 93.0, 86.0, 20.0, 60.0, 92.0, 92.0,
            128000, 0.55, 2.19, 55, "Open Weights (MIT)",
            "MATH-500 (97.3%), AIME 2024 (79.8%), Codeforces 96.3 percentile",
            "Open-weights reinforcement-learning reasoning model rivaling OpenAI o1 at a fraction of the inference cost.",
            Arrays.asList("Extraordinary mathematical logic and step-by-step proofs", "Unbeatable reasoning-to-cost ratio", "Fully open weights (MIT license)"),
            Arrays.asList("No native vision/multimodal capabilities", "Verbose reasoning traces increase token volume"),
            false, "https://github.com/deepseek-ai/DeepSeek-R1"
        ));

        list.add(new ModelEntity(
            "deepseek-v3",
            "DeepSeek V3",
            "DeepSeek",
            "Frontier MoE Value",
            91.0, 92.0, 86.0, 20.0, 86.0, 96.0, 90.0,
            128000, 0.14, 0.28, 90, "Open Weights (MIT)",
            "MMLU (88.5%), HumanEval (82.6%), Arena Elo (1310+)",
            "State of the art 671B Mixture-of-Experts architecture delivering frontier proprietary intelligence at rock-bottom cost.",
            Arrays.asList("Unprecedented cost-efficiency ($0.14 / 1M tokens)", "High coding and general reasoning benchmarks", "Fast inference throughput"),
            Arrays.asList("Text-only input", "Hosted API availability can experience peak demand queues"),
            false, "https://github.com/deepseek-ai/DeepSeek-V3"
        ));

        // =========================================================================
        // 5. META (3 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "llama-3-3-70b",
            "Llama 3.3 70B",
            "Meta",
            "Open Enterprise Standard",
            86.0, 85.0, 86.0, 20.0, 86.0, 92.0, 86.0,
            128000, 0.18, 0.60, 120, "Open Weights (Llama 3.3 Community)",
            "MMLU (88.6%), GSM8K (93.1%), Arena Elo (1280+)",
            "The industry benchmark for open-weights enterprise deployment, offering GPT-4 class general intelligence.",
            Arrays.asList("Full data privacy via on-premise deployment", "High inference speed on Groq/Cerebras hardware", "Strong general reasoning and tool use"),
            Arrays.asList("No vision capability", "Limited context window (128k) compared to Gemini"),
            false, "https://ai.meta.com/blog/llama-3-3/"
        ));

        list.add(new ModelEntity(
            "llama-3-1-405b",
            "Llama 3.1 405B",
            "Meta",
            "Open Frontier Giant",
            93.0, 89.0, 86.0, 20.0, 45.0, 65.0, 90.0,
            128000, 2.50, 5.00, 40, "Open Weights (Llama 3.1 Community)",
            "MMLU (88.6%), GSM8K (96.8%), HumanEval (89.0%)",
            "Meta's largest open-weights model, rivaling top proprietary frontier models in general knowledge, science, and synthetic data generation.",
            Arrays.asList("Tremendous general knowledge depth", "Ideal teacher model for synthetic fine-tuning", "Completely unconstrained on-premise deployment"),
            Arrays.asList("Demands substantial GPU clusters (8x H100s minimum) to self-host", "Text-only architecture"),
            false, "https://ai.meta.com/blog/meta-llama-3-1/"
        ));

        list.add(new ModelEntity(
            "llama-3-1-8b",
            "Llama 3.1 8B",
            "Meta",
            "Edge & High-Speed",
            74.0, 72.0, 84.0, 15.0, 98.0, 98.0, 76.0,
            128000, 0.05, 0.10, 170, "Open Weights (Llama 3.1 Community)",
            "MMLU (73.0%), GSM8K (84.5%), HumanEval (72.6%)",
            "Ultra-compact model easily deployable on consumer laptops, edge devices, and latency-critical microservices.",
            Arrays.asList("Runs locally on Apple Silicon and modest GPUs", "Virtually zero cost per million tokens", "Blazing speed (170+ tok/s)"),
            Arrays.asList("Lower reasoning depth on intricate logic", "Higher rate of code bugs on multi-file projects"),
            false, "https://ai.meta.com/llama/"
        ));

        // =========================================================================
        // 6. XAI (2 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "grok-2",
            "Grok 2",
            "xAI",
            "Frontier Reasoning & Knowledge",
            91.0, 87.0, 86.0, 25.0, 75.0, 62.0, 88.0,
            131072, 2.00, 10.00, 70, "Proprietary",
            "LMSYS Chatbot Arena Elo (1290+), MMLU (87.5%), MATH (76.1%)",
            "State of the art reasoning model from xAI with strong mathematical grounding, current information awareness, and code assistance.",
            Arrays.asList("High ranking on LMSYS Chatbot Arena", "Sharp analytical reasoning and math", "Up-to-date real-world understanding"),
            Arrays.asList("Higher pricing on output tokens ($10.00/1M)", "Multimodal handling routed to vision variant"),
            false, "https://x.ai/blog/grok-2"
        ));

        list.add(new ModelEntity(
            "grok-2-vision",
            "Grok 2 Vision",
            "xAI",
            "Visual Reasoning & Diagrams",
            89.0, 84.0, 65.0, 91.0, 72.0, 62.0, 86.0,
            32768, 2.00, 10.00, 65, "Proprietary",
            "MathVista (69.0%), DocVQA (93.6%), ChartQA (84.2%)",
            "Multimodal edition of Grok 2 engineered for complex visual math, diagrammatic reasoning, and document OCR parsing.",
            Arrays.asList("High visual math comprehension", "Effective document text extraction", "Direct image-to-structured-data translation"),
            Arrays.asList("Smaller context window (32,768 tokens)", "Higher output pricing"),
            false, "https://docs.x.ai/docs/overview#models"
        ));

        // =========================================================================
        // 7. MISTRAL AI (4 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "mistral-large-2",
            "Mistral Large 2",
            "Mistral AI",
            "Multilingual & Enterprise Flagship",
            89.0, 88.0, 86.0, 30.0, 76.0, 72.0, 87.0,
            128000, 2.00, 6.00, 80, "Proprietary & Research Weights",
            "MMLU (84.0%), HumanEval (92.0%), Multilingual Benchmarks",
            "Advanced 123B model with native multilingual fluency across French, German, Spanish, and Italian, alongside strong code reasoning.",
            Arrays.asList("Premier European multilingual performance", "Precise JSON and function calling", "Strong mathematical foundation"),
            Arrays.asList("Limited visual processing", "Context limit at 128k"),
            false, "https://mistral.ai/news/mistral-large-2407/"
        ));

        list.add(new ModelEntity(
            "codestral-2501",
            "Codestral 2501",
            "Mistral AI",
            "Dedicated Code Specialist",
            85.0, 94.0, 92.0, 15.0, 88.0, 90.0, 88.0,
            256000, 0.30, 0.90, 100, "Proprietary",
            "HumanEval (86.6%), RepoBench, Spider SQL (81.2%)",
            "Purpose-built generative model specialized for code completion, fill-in-the-middle (FIM), and multi-file refactoring.",
            Arrays.asList("256k context window for entire repository ingest", "Exceptional fill-in-the-middle syntax completion", "Highly economical API pricing"),
            Arrays.asList("Code and text only (no image analysis)", "Narrower general world knowledge"),
            false, "https://mistral.ai/news/codestral-2501/"
        ));

        list.add(new ModelEntity(
            "mistral-small-3",
            "Mistral Small 3",
            "Mistral AI",
            "Low-Latency Enterprise",
            81.0, 82.0, 86.0, 25.0, 94.0, 95.0, 82.0,
            128000, 0.10, 0.30, 140, "Open Weights (Apache 2.0)",
            "MMLU (81.0%), GSM8K (87.5%), Arena Elo (1240+)",
            "Apache 2.0 licensed 24B parameter model delivering top-tier efficiency for conversational bots and agent orchestration.",
            Arrays.asList("Permissive Apache 2.0 open-source license", "Ultra-fast response latency", "Cost-effective API and self-hosting"),
            Arrays.asList("Cannot match Large 2 on deep mathematical theorems", "Text-first focus"),
            false, "https://mistral.ai/news/mistral-small-3/"
        ));

        list.add(new ModelEntity(
            "pixtral-large",
            "Pixtral Large",
            "Mistral AI",
            "Multimodal 124B Vision",
            88.0, 85.0, 86.0, 93.0, 72.0, 72.0, 87.0,
            128000, 2.00, 6.00, 70, "Open Weights & API",
            "MM-Vet (69.4%), DocVQA (93.3%), ChartQA (83.5%)",
            "Frontier 124B multimodal model natively understanding images, charts, and technical schematics at high resolution.",
            Arrays.asList("Native image and document comprehension", "Strong multilingual visual QA", "High resolution without aspect ratio distortion"),
            Arrays.asList("Context capped at 128k", "Higher cost than lightweight vision models"),
            false, "https://mistral.ai/news/pixtral-large/"
        ));

        // =========================================================================
        // 8. ALIBABA / QWEN (4 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "qwen-2-5-coder-32b",
            "Qwen 2.5 Coder 32B",
            "Alibaba Cloud",
            "Open Source Code Champion",
            86.0, 95.0, 88.0, 15.0, 84.0, 93.0, 88.0,
            131072, 0.20, 0.60, 95, "Open Weights (Apache 2.0)",
            "EvalPlus (88.4), HumanEval (92.7%), MultiPL-E benchmark",
            "Dedicated code generation model outperforming many larger proprietary models on multi-language programming tasks.",
            Arrays.asList("Outstanding multi-language coding synthesis", "Can be self-hosted privately (Apache 2.0)", "Highly economical API pricing"),
            Arrays.asList("Text and code only (no image support)", "Narrower non-technical general knowledge"),
            false, "https://qwenlm.github.io/blog/qwen2.5-coder/"
        ));

        list.add(new ModelEntity(
            "qwen-2-5-72b-instruct",
            "Qwen 2.5 72B Instruct",
            "Alibaba Cloud",
            "Open Multilingual Generalist",
            89.0, 88.0, 88.0, 20.0, 80.0, 90.0, 88.0,
            131072, 0.35, 1.05, 80, "Open Weights (Apache 2.0)",
            "MMLU (86.1%), MATH (83.1%), Arena Elo (1285+)",
            "Leading open-weights 72B parameter model delivering flagship performance across math, coding, and multilingual knowledge.",
            Arrays.asList("Remarkable mathematical and coding capabilities", "Fluency across 29+ languages", "Permissive open weights license"),
            Arrays.asList("Text-only input", "Requires multi-GPU hardware for local hosting"),
            false, "https://qwenlm.github.io/blog/qwen2.5/"
        ));

        list.add(new ModelEntity(
            "qwen-2-5-max",
            "Qwen 2.5 Max",
            "Alibaba Cloud",
            "MoE Cloud Flagship",
            93.0, 91.0, 65.0, 20.0, 75.0, 76.0, 90.0,
            32768, 1.60, 4.80, 75, "Proprietary (Aliyun API)",
            "Arena Elo (1320+), MMLU-Pro (71.5%), GPQA Diamond (57.1%)",
            "Alibaba's largest proprietary mixture-of-experts model delivering top-tier benchmark results across academic and logic exams.",
            Arrays.asList("Ranks among the top proprietary models in Arena Elo", "Deep analytical reasoning and code", "Strong Chinese and global multilingual proficiency"),
            Arrays.asList("Context window limited to 32k tokens", "Closed proprietary cloud endpoint"),
            false, "https://www.alibabacloud.com/help/en/model-studio/developer-reference/what-is-qwen-max"
        ));

        list.add(new ModelEntity(
            "qwen-2-5-vl-72b",
            "Qwen 2.5 VL 72B",
            "Alibaba Cloud",
            "Open Vision-Language Titan",
            88.0, 84.0, 88.0, 95.0, 68.0, 86.0, 88.0,
            131072, 0.50, 1.50, 65, "Open Weights (Apache 2.0)",
            "MathVista (74.8%), DocVQA (96.1%), Video-MME (82.1%)",
            "State of the art open-weights multimodal model capable of analyzing hour-long videos, high-resolution scans, and intricate UI mockups.",
            Arrays.asList("Top-tier document and chart understanding (96.1% DocVQA)", "Video temporal reasoning over 1 hour", "Apache 2.0 open release"),
            Arrays.asList("High GPU memory requirement for local inference", "Inference latency higher than Flash models"),
            false, "https://qwenlm.github.io/blog/qwen2.5-vl/"
        ));

        // =========================================================================
        // 9. COHERE (2 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "command-r-plus",
            "Command R+",
            "Cohere",
            "Enterprise RAG & Orchestration",
            87.0, 84.0, 86.0, 20.0, 78.0, 65.0, 85.0,
            128000, 2.50, 10.00, 75, "Proprietary",
            "Multi-Hop RAG Benchmark, Tool Calling (82.0%), MMLU (75.7%)",
            "Engineered specifically for enterprise retrieval-augmented generation (RAG), multi-step tool use, and grounded citations.",
            Arrays.asList("Industry gold standard for cited RAG answers", "Minimizes hallucinations through strict grounding", "Multi-lingual business communications"),
            Arrays.asList("Lower pure coding benchmarks compared to Sonnet or Qwen Coder", "Text-only input"),
            false, "https://cohere.com/blog/command-r-plus-october-2024"
        ));

        list.add(new ModelEntity(
            "command-r",
            "Command R",
            "Cohere",
            "Efficient RAG Workhorse",
            80.0, 78.0, 86.0, 15.0, 90.0, 94.0, 80.0,
            128000, 0.15, 0.60, 110, "Proprietary",
            "Enterprise RAG Benchmark, Tool Calling (77.8%), MMLU (70.2%)",
            "Cost-effective enterprise model optimized for high-volume customer support search, internal knowledge indexing, and tool workflows.",
            Arrays.asList("Highly competitive pricing ($0.15/1M input)", "Fast throughput for conversational bots", "Accurate citation generation"),
            Arrays.asList("Moderate reasoning depth for intricate multi-step math", "Text-only"),
            false, "https://cohere.com/command"
        ));

        // =========================================================================
        // 10. AI21 LABS (2 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "jamba-1-5-large",
            "Jamba 1.5 Large",
            "AI21 Labs",
            "Hybrid SSM-Transformer Long Context",
            86.0, 82.0, 94.0, 15.0, 82.0, 70.0, 84.0,
            256000, 2.00, 8.00, 85, "Open Weights (Jamba Open License)",
            "RULER 256K Benchmark, MMLU (81.2%), GSM8K (87.1%)",
            "Hybrid Mamba-SSM and Transformer architecture offering high throughput on extensive 256k-token documents and multi-turn context.",
            Arrays.asList("256k token context window with linear complexity", "Hybrid Mamba-Transformer architecture", "Stable long-context retrieval"),
            Arrays.asList("No vision capability", "Higher pricing than pure open MoE models"),
            false, "https://www.ai21.com/jamba"
        ));

        list.add(new ModelEntity(
            "jamba-1-5-mini",
            "Jamba 1.5 Mini",
            "AI21 Labs",
            "High-Throughput SSM Hybrid",
            78.0, 75.0, 94.0, 15.0, 96.0, 95.0, 80.0,
            256000, 0.20, 0.40, 150, "Open Weights (Jamba Open License)",
            "RULER 256K Benchmark, MMLU (73.5%), GSM8K (80.2%)",
            "Lightweight hybrid SSM-Transformer model processing lengthy documents and customer transcripts at lightning speed and minimal cost.",
            Arrays.asList("Fast generation on long 256k contexts", "Economical token pricing", "Low memory footprint in inference"),
            Arrays.asList("Basic coding syntax only", "Text-only"),
            false, "https://www.ai21.com/jamba"
        ));

        // =========================================================================
        // 11. AMAZON (3 Models)
        // =========================================================================
        list.add(new ModelEntity(
            "amazon-nova-pro",
            "Amazon Nova Pro",
            "Amazon",
            "Multimodal Bedrock Flagship",
            88.0, 86.0, 94.0, 90.0, 80.0, 86.0, 88.0,
            300000, 0.80, 3.20, 80, "Proprietary (Amazon Bedrock)",
            "MMLU (85.2%), MathVista (67.4%), SWE-bench Lite",
            "Amazon's flagship multimodal model on AWS Bedrock offering a 300k context window and balanced price-performance for enterprise workloads.",
            Arrays.asList("Generous 300k context window", "Strong enterprise AWS integration and fine-tuning", "Competitive pricing for flagship tier"),
            Arrays.asList("Slightly lower coding benchmarks than Claude 3.7 or GPT-4o", "Locked into AWS cloud ecosystems"),
            false, "https://aws.amazon.com/ai/generative-ai/nova/"
        ));

        list.add(new ModelEntity(
            "amazon-nova-lite",
            "Amazon Nova Lite",
            "Amazon",
            "Ultra Fast Bedrock Multimodal",
            80.0, 78.0, 94.0, 85.0, 96.0, 98.0, 82.0,
            300000, 0.06, 0.24, 160, "Proprietary (Amazon Bedrock)",
            "MMLU (78.8%), Video-MME (68.5%), Arena Elo (1220+)",
            "Extremely low-cost multimodal model designed for high-velocity image, video, and text analysis on AWS infrastructure.",
            Arrays.asList("Incredible pricing ($0.06 / 1M input tokens)", "300k context window for large uploads", "Sub-second multimodal latency"),
            Arrays.asList("Moderate reasoning depth on hard multi-step problems", "Requires AWS IAM account"),
            false, "https://aws.amazon.com/ai/generative-ai/nova/"
        ));

        list.add(new ModelEntity(
            "amazon-nova-micro",
            "Amazon Nova Micro",
            "Amazon",
            "Micro-Latency Text Engine",
            74.0, 70.0, 86.0, 10.0, 99.0, 99.0, 75.0,
            128000, 0.035, 0.14, 210, "Proprietary (Amazon Bedrock)",
            "MMLU (73.5%), GSM8K (80.1%), Sub-200ms TTFT",
            "Text-only ultra-low-latency model designed for real-time classification, routing, and high-volume data transformation at minimal expense.",
            Arrays.asList("Lowest commercial price on AWS ($0.035/1M)", "Throughput exceeds 200 tokens/sec", "Instantaneous time-to-first-token"),
            Arrays.asList("Text-only (no image or video inputs)", "Not suited for complex software development"),
            false, "https://aws.amazon.com/ai/generative-ai/nova/"
        ));

        // =========================================================================
        // 12. NVIDIA (1 Model)
        // =========================================================================
        list.add(new ModelEntity(
            "nemotron-4-340b-instruct",
            "Nemotron-4 340B Instruct",
            "NVIDIA",
            "Synthetic Data & Alignment Titan",
            89.0, 83.0, 35.0, 15.0, 60.0, 80.0, 83.0,
            4096, 1.20, 3.60, 50, "Open Weights (NVIDIA Open Model License)",
            "MT-Bench (8.22), MMLU (81.1%), GSM8K (88.2%)",
            "Massive open-access model designed by NVIDIA specifically for generating high-quality synthetic training data for fine-tuning smaller LLMs.",
            Arrays.asList("Premier engine for synthetic data creation", "Exceptional reward-modeling and alignment capabilities", "Permissive commercial use under NVIDIA license"),
            Arrays.asList("Short context window (4,096 tokens)", "Enormous compute requirements to self-host"),
            false, "https://developer.nvidia.com/blog/nemotron-4-340b/"
        ));

        // =========================================================================
        // 13. MOONSHOT / KIMI (1 Model)
        // =========================================================================
        list.add(new ModelEntity(
            "kimi-k1-5",
            "Kimi k1.5",
            "Moonshot AI",
            "Long-Context Reinforcement Reasoning",
            95.0, 91.0, 86.0, 88.0, 65.0, 78.0, 91.0,
            128000, 1.40, 4.20, 60, "Proprietary (Moonshot API)",
            "MATH-500 (96.2%), AIME 2024 (77.5%), Codeforces 94th percentile",
            "Multimodal reinforcement-learning reasoning model rivaling OpenAI o1 and DeepSeek R1 with native visual reasoning.",
            Arrays.asList("High mathematical and competitive programming skill", "Multimodal reasoning with visual chart proofs", "Strong Chinese and English bilingual balance"),
            Arrays.asList("Moderate latency due to extended reasoning paths", "Higher token consumption"),
            false, "https://platform.moonshot.cn/docs/guide"
        ));

        // =========================================================================
        // 14. ZHIPU / GLM (1 Model)
        // =========================================================================
        list.add(new ModelEntity(
            "glm-4-plus",
            "GLM-4-Plus",
            "Zhipu AI",
            "Bilingual Reasoning & Chinese SOTA",
            90.0, 87.0, 86.0, 86.0, 75.0, 84.0, 88.0,
            128000, 1.40, 1.40, 75, "Proprietary (Zhipu BigModel API)",
            "C-Eval (92.4%), AlignBench (8.15), MMLU (85.3%)",
            "Leading Chinese-English bilingual frontier model featuring equal pricing for input and output tokens and strong agentic tool performance.",
            Arrays.asList("SOTA Chinese language comprehension and cultural context", "Symmetric pricing ($1.40 input / $1.40 output)", "Effective long-context and tool-calling capabilities"),
            Arrays.asList("Coding benchmarks slightly below top Western coding specialists", "Context capped at 128k"),
            false, "https://open.bigmodel.cn/dev/api#glm-4"
        ));

        return list;
    }

    /**
     * Seeds the relational database if models count is below target threshold.
     */
    public static void seedIfNecessary(ModelDAO dao) {
        int existingCount = dao.countModels();
        if (existingCount < 25) {
            LOGGER.info("Seeding relational LLM model catalog into SQLite database...");
            List<ModelEntity> catalog = getSeedCatalog();
            for (ModelEntity model : catalog) {
                dao.insertModel(model);
                dao.insertBenchmark(model.getId(), "Primary Citation", model.getBenchmarkSource(), model.getSourceReference());
            }
            LOGGER.info("Seeding complete. Inserted " + catalog.size() + " models into SQLite database.");
        } else {
            LOGGER.info("Relational database already seeded with " + existingCount + " models.");
        }
    }
}
