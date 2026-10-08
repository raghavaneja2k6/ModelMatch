# ModelMatch — AI-Powered Large Language Model Recommendation Engine

> **“Tell us what you want to do, and ModelMatch tells you which AI model is best suited for it—and why.”**

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/)
[![Database](https://img.shields.io/badge/Database-SQLite%20JDBC-blue.svg)](https://sqlite.org/)
[![Models](https://img.shields.io/badge/Catalog-38%20Models-success.svg)](#-empirical-llm-database-38-frontier-models-across-14-providers)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📌 Project Overview
**ModelMatch** is an enterprise-grade, Java-centered decision-support system designed to solve the **LLM Selection Dilemma**. In a global artificial intelligence ecosystem characterized by dozens of models offering conflicting trade-offs across reasoning depth, coding synthesis, context capacity, inference latency, and token pricing, ModelMatch analyzes unstructured natural language task specifications, projects them into a multidimensional 6D requirement vector $[R, C, X, M, S, P]$, computes geometric cosine similarity against a relational LLM database, applies user-customized multi-criteria utility scoring, and delivers transparent, explainable recommendations.

### ⚠️ Core Architecture Principle: Recommendation vs. Execution
ModelMatch is fundamentally an **independent LLM recommendation and ranking engine**.
* **Model Recommendation**: ModelMatch evaluates and ranks **38 contemporary models across 14 major global AI providers** (OpenAI, Anthropic, Google, DeepSeek, Meta, xAI, Mistral, Alibaba Cloud, Cohere, AI21 Labs, Amazon Bedrock, NVIDIA, Moonshot AI, and Zhipu AI).
* **Model Execution**: ModelMatch does **not** claim to execute every external proprietary or self-hosted model in its catalog. The catalog represents models available for empirical evaluation and recommendation. Direct API execution is currently supported for Google Gemini via `GeminiService`.
* **Algorithmic Autonomy**: The winning model is strictly selected by the Java recommendation engine (`RecommendationEngine.rankModels()`). Google Gemini is utilized exclusively as an auxiliary AI architectural explainer to provide technical match rationales for the winning model.

---

## 🏗️ System Architecture

```
                                [ User Task Description ]
                                           │
                                           ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ 1. PRESENTATION LAYER (ChatGPT Replica UI)                                             │
│    Vanilla HTML5, CSS3, ES6 JavaScript                                                 │
│    Zero layout shift, responsive dark theme, parameter sliders, cost estimation widget │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │ HTTP REST (JSON)
                                           ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ 2. EMBEDDED WEB & CONCURRENCY LAYER                                                    │
│    com.modelmatch.server.ModelMatchServer (com.sun.net.httpserver.HttpServer)           │
│    Executors.newVirtualThreadPerTaskExecutor() (Java 21 LTS Virtual Threads)           │
│    ApiHandler.java REST Controller (/api/recommend, /api/models, /api/health)          │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │
                                           ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ 3. NLP & TASK INTELLIGENCE LAYER                                                       │
│    • Java TF-IDF Vectorizer & Suffix Stemmer (TfIdfClassifier.java + TaskCorpus)        │
│    • Maps prompt to 6D Requirement Vector: U = [R, C, X, M, S, P] ∈ [0, 1]^6          │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │
                                           ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ 4. DUAL-PHASE RECOMMENDATION & SCORING ENGINE                                          │
│    • Vector Cosine Similarity: Sim(U, M) = (U · M) / (||U|| * ||M||)                   │
│    • Multi-Criteria Utility Preference: Score = Σ (w_i * S_i)                          │
│    • Hard Constraint Multipliers: Penalties for multimodal & context limits            │
│    • Hybrid Synthesis: FinalScore = (0.50 * WeightedScore + 0.50 * CosineScore) * Φ   │
│    • Evaluates ALL 38 models uniformly with zero bias                                  │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │
                                           ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ 5. RELATIONAL DATA PERSISTENCE & EXPLAINABILITY LAYER                                  │
│    • DatabaseManager & ModelDAO: SQLite JDBC with PreparedStatements (data/modelmatch.db)│
│    • Tables: models, model_capabilities, model_benchmarks, recommendation_logs         │
│    • ExplainabilityService.java: Mathematical derivation & trade-off breakdown         │
│    • GeminiService.java: Auxiliary AI architectural assessment of the winning model    │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔒 Security Architecture (Zero-Secret Policy)

ModelMatch adheres strictly to enterprise API security standards:
1. **Server-Side API Key Isolation**: The Google Gemini API key resides **strictly on the backend** and is resolved via the `GEMINI_API_KEY` server environment variable or a local gitignored `.env` file.
2. **Zero Frontend Exposure**: The API key is **never** transmitted to the browser, stored in client-side cookies/localStorage/sessionStorage, or returned in any API response.
3. **Optional Ephemeral User Key**: If a user supplies their own Gemini key via the client UI, it is passed over the request, used only for that single call in memory, and immediately garbage-collected without logging or database persistence.
4. **Deterministic Fallback**: If no Gemini API key is configured, the system operates seamlessly in offline mode using its pure Java explainability synthesizer.

---

## 🧮 Mathematical Recommendation Formulations

### 1. 6D Capability & Requirement Vectors
Both user task requirements $\vec{U}$ and model capabilities $\vec{M}$ are projected into a normalized 6-dimensional vector space $[0.0, 1.0]^6$:
* $R$: Reasoning & Formal Logic
* $C$: Code Generation & Synthesis
* $X$: Context Window Capacity
* $M$: Multimodal / Vision Comprehension
* $S$: Inference Throughput & Low Latency
* $P$: Cost Efficiency (normalized inverse of token dollar cost)

### 2. Vector Cosine Similarity
$$\text{Similarity}(\vec{U}, \vec{M}) = \frac{\vec{U} \cdot \vec{M}}{\|\vec{U}\| \|\vec{M}\|} = \frac{\sum_{i=1}^6 u_i m_i}{\sqrt{\sum_{i=1}^6 u_i^2} \sqrt{\sum_{i=1}^6 m_i^2}}$$

### 3. User Priority Weighted Multi-Criteria Utility
$$\text{WeightedScore} = \sum_{i=1}^6 w_i S_i = w_r R + w_c C + w_x X + w_m M + w_s S + w_p P$$
Where $\sum w_i = 1.0$ and $S_i \in [0, 100]$.

### 4. Hard Constraint Multipliers ($\Phi$)
* $\Phi_{\text{vision}} = 0.65$ if task requires multimodal input and model is text-only ($M < 30$).
* $\Phi_{\text{context}} = 0.85$ if task demands massive context ($>200\text{k}$) and model context $< 200\text{k}$.

### 5. Hybrid Synthesis Final Ranking
$$\text{FinalScore} = \left( 0.50 \times \text{WeightedScore} + 0.50 \times (\text{CosineSimilarity} \times 100) \right) \times \Phi$$

---

## 🗄️ Relational Database Schema (SQLite JDBC)

The model catalog is genuinely persisted in a normalized relational database using SQLite JDBC (`org.xerial:sqlite-jdbc`) and managed through `DatabaseManager.java` and `ModelDAO.java` with prepared statements:

```sql
-- 1. Master Model Catalog
CREATE TABLE models (
    model_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    badge VARCHAR(100),
    category VARCHAR(50),
    context_window_tokens INTEGER NOT NULL,
    cost_input_per_million REAL NOT NULL,
    cost_output_per_million REAL NOT NULL,
    speed_tokens_per_sec INTEGER NOT NULL,
    license_type VARCHAR(100),
    benchmark_source VARCHAR(255),
    description TEXT,
    strengths_json TEXT,
    weaknesses_json TEXT,
    direct_execution_supported INTEGER DEFAULT 0,
    source_reference VARCHAR(255)
);

-- 2. Normalized 6D Capability Vectors
CREATE TABLE model_capabilities (
    model_id VARCHAR(50) PRIMARY KEY,
    reasoning_score REAL NOT NULL,
    coding_score REAL NOT NULL,
    context_score REAL NOT NULL,
    multimodal_score REAL NOT NULL,
    speed_score REAL NOT NULL,
    cost_score REAL NOT NULL,
    overall_score REAL NOT NULL,
    FOREIGN KEY (model_id) REFERENCES models(model_id) ON DELETE CASCADE
);

-- 3. Verified Benchmark Citations
CREATE TABLE model_benchmarks (
    benchmark_id INTEGER PRIMARY KEY AUTOINCREMENT,
    model_id VARCHAR(50) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    score_value VARCHAR(100) NOT NULL,
    source_citation VARCHAR(255),
    FOREIGN KEY (model_id) REFERENCES models(model_id) ON DELETE CASCADE
);

-- 4. Audit & Telemetry Logs
CREATE TABLE recommendation_logs (
    query_id VARCHAR(64) PRIMARY KEY,
    prompt_text TEXT,
    detected_category VARCHAR(50),
    top_model_id VARCHAR(50),
    match_score REAL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 📊 Empirical LLM Database (38 Frontier Models across 14 Providers)

| Model Name | Provider | Reasoning | Coding | Context | Vision | Speed | Pricing (In/Out 1M) | Direct API | Primary Benchmark Source |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **Claude 3.7 Sonnet** | Anthropic | 97 | 98 | 200K | 93 | 75 tps | $3.00 / $15.00 | No | SWE-bench Verified (70.3%), TAU-bench |
| **Claude 3.5 Sonnet** | Anthropic | 94 | 95 | 200K | 94 | 80 tps | $3.00 / $15.00 | No | SWE-bench Verified (49.0%), GPQA Diamond |
| **Claude 3.5 Haiku** | Anthropic | 83 | 85 | 200K | 82 | 140 tps | $0.80 / $4.00 | No | SWE-bench Verified (40.6%), LiveCodeBench |
| **Claude 3 Opus** | Anthropic | 91 | 86 | 200K | 88 | 40 tps | $15.00 / $75.00 | No | MMLU (86.8%), GPQA (50.4%) |
| **GPT-4o** | OpenAI | 92 | 90 | 128K | 96 | 110 tps | $2.50 / $10.00 | No | LMSYS Chatbot Arena Elo (1335+), MMLU-Pro |
| **GPT-4o-mini** | OpenAI | 79 | 80 | 128K | 84 | 135 tps | $0.15 / $0.60 | No | MMLU (82.0%), HumanEval (87.2%) |
| **OpenAI o1** | OpenAI | 98 | 93 | 200K | 90 | 45 tps | $15.00 / $60.00 | No | AIME 2024 (83.3%), Codeforces 93.3% |
| **OpenAI o3-mini** | OpenAI | 95 | 94 | 200K | 10 | 80 tps | $1.10 / $4.40 | No | Codeforces 2083 Rating, AIME 2024 |
| **GPT-4.5** | OpenAI | 94 | 90 | 128K | 95 | 50 tps | $75.00 / $150.00 | No | SimpleQA (37.1%), MMLU (89.2%) |
| **Gemini 2.5 Pro** | Google | 94 | 92 | 2M | 95 | 65 tps | $1.25 / $5.00 | **Yes** | 2M Needle Recall (99.7%), GPQA Diamond |
| **Gemini 3.5 Flash** | Google | 88 | 87 | 1M | 92 | 150 tps | $0.075 / $0.30 | **Yes** | 1M Needle Recall (99.8%), Arena Elo |
| **Gemini 2.0 Flash** | Google | 86 | 85 | 1M | 94 | 145 tps | $0.10 / $0.40 | **Yes** | MMLU (82.5%), MathVista (68.1%) |
| **Gemini 1.5 Pro** | Google | 90 | 88 | 2M | 92 | 60 tps | $1.25 / $5.00 | **Yes** | MMLU (85.9%), Video-MME (81.3%) |
| **DeepSeek R1** | DeepSeek | 97 | 93 | 128K | 20 | 55 tps | $0.55 / $2.19 | No | MATH-500 (97.3%), AIME 2024 (79.8%) |
| **DeepSeek V3** | DeepSeek | 91 | 92 | 128K | 20 | 90 tps | $0.14 / $0.28 | No | 671B MoE, MMLU (88.5%), HumanEval |
| **Llama 3.3 70B** | Meta | 86 | 85 | 128K | 20 | 120 tps | $0.18 / $0.60 | No | MMLU (88.6%), GSM8K (93.1%) |
| **Llama 3.1 405B** | Meta | 93 | 89 | 128K | 20 | 40 tps | $2.50 / $5.00 | No | MMLU (88.6%), HumanEval (89.0%) |
| **Llama 3.1 8B** | Meta | 74 | 72 | 128K | 15 | 170 tps | $0.05 / $0.10 | No | MMLU (73.0%), GSM8K (84.5%) |
| **Grok 2** | xAI | 91 | 87 | 131K | 25 | 70 tps | $2.00 / $10.00 | No | LMSYS Chatbot Arena Elo (1290+) |
| **Grok 2 Vision** | xAI | 89 | 84 | 32K | 91 | 65 tps | $2.00 / $10.00 | No | MathVista (69.0%), DocVQA (93.6%) |
| **Mistral Large 2** | Mistral AI | 89 | 88 | 128K | 30 | 80 tps | $2.00 / $6.00 | No | Multilingual HumanEval & MMLU |
| **Codestral 2501** | Mistral AI | 85 | 94 | 256K | 15 | 100 tps | $0.30 / $0.90 | No | RepoBench, HumanEval (86.6%) |
| **Mistral Small 3** | Mistral AI | 81 | 82 | 128K | 25 | 140 tps | $0.10 / $0.30 | No | MMLU (81.0%), GSM8K (87.5%) |
| **Pixtral Large** | Mistral AI | 88 | 85 | 128K | 93 | 70 tps | $2.00 / $6.00 | No | MM-Vet (69.4%), DocVQA (93.3%) |
| **Qwen 2.5 Coder 32B** | Alibaba | 86 | 95 | 131K | 15 | 95 tps | $0.20 / $0.60 | No | EvalPlus (88.4), HumanEval (92.7%) |
| **Qwen 2.5 72B Instruct** | Alibaba | 89 | 88 | 131K | 20 | 80 tps | $0.35 / $1.05 | No | MMLU (86.1%), MATH (83.1%) |
| **Qwen 2.5 Max** | Alibaba | 93 | 91 | 32K | 20 | 75 tps | $1.60 / $4.80 | No | Arena Elo (1320+), MMLU-Pro (71.5%) |
| **Qwen 2.5 VL 72B** | Alibaba | 88 | 84 | 131K | 95 | 65 tps | $0.50 / $1.50 | No | MathVista (74.8%), DocVQA (96.1%) |
| **Command R+** | Cohere | 87 | 84 | 128K | 20 | 75 tps | $2.50 / $10.00 | No | Enterprise Multi-Hop RAG Benchmark |
| **Command R** | Cohere | 80 | 78 | 128K | 15 | 110 tps | $0.15 / $0.60 | No | Enterprise RAG Benchmark, Tool Calling |
| **Jamba 1.5 Large** | AI21 Labs | 86 | 82 | 256K | 15 | 85 tps | $2.00 / $8.00 | No | Hybrid SSM-Transformer, RULER 256K |
| **Jamba 1.5 Mini** | AI21 Labs | 78 | 75 | 256K | 15 | 150 tps | $0.20 / $0.40 | No | High-Throughput SSM Hybrid |
| **Amazon Nova Pro** | Amazon | 88 | 86 | 300K | 90 | 80 tps | $0.80 / $3.20 | No | MMLU (85.2%), Bedrock Multimodal |
| **Amazon Nova Lite** | Amazon | 80 | 78 | 300K | 85 | 160 tps | $0.06 / $0.24 | No | MMLU (78.8%), Video-MME (68.5%) |
| **Amazon Nova Micro** | Amazon | 74 | 70 | 128K | 10 | 210 tps | $0.035 / $0.14 | No | Sub-200ms TTFT, High Throughput |
| **Nemotron-4 340B** | NVIDIA | 89 | 83 | 4K | 15 | 50 tps | $1.20 / $3.60 | No | MT-Bench (8.22), Synthetic Alignment |
| **Kimi k1.5** | Moonshot AI | 95 | 91 | 128K | 88 | 60 tps | $1.40 / $4.20 | No | MATH-500 (96.2%), AIME 2024 (77.5%) |
| **GLM-4-Plus** | Zhipu AI | 90 | 87 | 128K | 86 | 75 tps | $1.40 / $1.40 | No | C-Eval (92.4%), AlignBench (8.15) |

---

## 🛠️ Project Setup & Quickstart

### Prerequisites
- **JDK 21 LTS** or higher
- Git & Maven

### 1. Clone the Repository
```bash
git clone https://github.com/raghavaneja2k6/ModelMatch.git
cd ModelMatch
```

### 2. Configure Environment Variables
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Optionally provide your Google Gemini API key:
```env
PORT=8080
GEMINI_API_KEY=your_gemini_api_key_here
```
*(If no API key is provided, ModelMatch will automatically run in local mode with pure Java rule-based explanations).*

### 3. Build & Run
```bash
# Run unit & integration tests
mvn test

# Package executable fat JAR
mvn clean package

# Start the application
java -jar target/modelmatch-1.0.0-jar-with-dependencies.jar
```
Open your browser to: **`http://localhost:8080`**

---

## 📡 REST API Reference

| Endpoint | Method | Description |
| :--- | :---: | :--- |
| `/api/health` | `GET` | Health status, Java version, models loaded, SQLite database status |
| `/api/models` | `GET` | Returns all 38 models dynamically from SQLite |
| `/api/models/{id}` | `GET` | Returns single model details |
| `/api/recommend` | `POST` | Recommends optimal model based on prompt and custom weights |
| `/api/compare` | `POST` | Head-to-head comparison of any two model IDs |
| `/api/classify` | `POST` | Analyzes task category confidence using TF-IDF |

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
