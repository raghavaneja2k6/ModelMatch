package com.modelmatch.classifier;

import com.modelmatch.model.TaskCategory;

import java.util.*;

/**
 * Supervised training corpus for the Java TF-IDF NLP task classifier.
 */
public class TaskCorpus {

    public static class Document {
        private final TaskCategory category;
        private final String text;

        public Document(TaskCategory category, String text) {
            this.category = category;
            this.text = text;
        }

        public TaskCategory getCategory() { return category; }
        public String getText() { return text; }
    }

    public static List<Document> getTrainingDocuments() {
        List<Document> docs = new ArrayList<>();

        // CODING
        docs.add(new Document(TaskCategory.CODING,
            "build a production-ready REST API in Java Spring Boot with JWT authentication and PostgreSQL database"));
        docs.add(new Document(TaskCategory.CODING,
            "write Python script to debug memory leak in asynchronous microservice and fix concurrency race condition"));
        docs.add(new Document(TaskCategory.CODING,
            "implement Dijkstra algorithm and binary search tree with unit tests in C++ and TypeScript"));
        docs.add(new Document(TaskCategory.CODING,
            "refactor legacy frontend React code to Next.js with state management and optimized bundle size"));
        docs.add(new Document(TaskCategory.CODING,
            "design database schema for e-commerce website with SQL foreign keys indexes and stored procedures"));
        docs.add(new Document(TaskCategory.CODING,
            "develop Docker container CI CD pipeline with automated GitHub actions and Kubernetes deployment"));
        docs.add(new Document(TaskCategory.CODING,
            "write unit tests with JUnit Mockito pytest coverage testing software bugs code review"));
        docs.add(new Document(TaskCategory.CODING,
            "code an algorithm for graph traversal dynamic programming LeetCode hard problem optimization"));

        // REASONING
        docs.add(new Document(TaskCategory.REASONING,
            "solve advanced calculus differential equation step by step with mathematical induction proof"));
        docs.add(new Document(TaskCategory.REASONING,
            "formal logic deduction truth table predicate calculus syllogism philosophical argument validity"));
        docs.add(new Document(TaskCategory.REASONING,
            "complex probability puzzle Monty Hall Bayesian statistics hypothesis testing theorem derivation"));
        docs.add(new Document(TaskCategory.REASONING,
            "analyze game theory Nash equilibrium prisoner dilemma economic payoff matrix optimization strategy"));
        docs.add(new Document(TaskCategory.REASONING,
            "solve difficult arithmetic competition math Olympiad IMO algebra geometry number theory problem"));
        docs.add(new Document(TaskCategory.REASONING,
            "deductive reasoning puzzle crime mystery investigation logical contradiction hypothesis evaluation"));

        // WRITING
        docs.add(new Document(TaskCategory.WRITING,
            "write an engaging narrative essay about human consciousness and future of space travel"));
        docs.add(new Document(TaskCategory.WRITING,
            "draft persuasive executive business proposal and professional outreach email to venture capital investors"));
        docs.add(new Document(TaskCategory.WRITING,
            "create compelling marketing copy, social media ad captions, and SEO blog post for fitness brand"));
        docs.add(new Document(TaskCategory.WRITING,
            "write a dramatic screenplay dialogue between two estranged siblings reuniting in Paris"));
        docs.add(new Document(TaskCategory.WRITING,
            "compose evocative poetry with rhythmic cadence metaphors imagery lyrical structure"));
        docs.add(new Document(TaskCategory.WRITING,
            "edit and polish fiction chapter improve dialogue pacing character voice descriptive prose"));

        // RESEARCH
        docs.add(new Document(TaskCategory.RESEARCH,
            "summarize a 100-page academic research paper on quantum computing and superconducting qubits"));
        docs.add(new Document(TaskCategory.RESEARCH,
            "conduct literature review of recent advancements in cancer immunotherapy clinical trial results"));
        docs.add(new Document(TaskCategory.RESEARCH,
            "extract key financial figures quarterly revenue EBITDA risk factors from annual 10-K SEC filing"));
        docs.add(new Document(TaskCategory.RESEARCH,
            "synthesize multiple historical sources into comprehensive briefing on geopolitical treaties in Europe"));
        docs.add(new Document(TaskCategory.RESEARCH,
            "analyze large corpus of survey responses identify recurring themes customer sentiment trends"));
        docs.add(new Document(TaskCategory.RESEARCH,
            "digest lengthy legal contract identify liability indemnification clauses compliance risks"));

        // MULTIMODAL
        docs.add(new Document(TaskCategory.MULTIMODAL,
            "inspect and interpret complex financial charts line graphs and bar diagrams from quarterly report"));
        docs.add(new Document(TaskCategory.MULTIMODAL,
            "extract tables figures handwritten notes OCR from scanned multi-page PDF documents"));
        docs.add(new Document(TaskCategory.MULTIMODAL,
            "analyze medical X-ray and MRI scan imagery highlight structural anomalies visual indicators"));
        docs.add(new Document(TaskCategory.MULTIMODAL,
            "describe image contents in detail identify objects spatial layout color palette visual aesthetic"));
        docs.add(new Document(TaskCategory.MULTIMODAL,
            "extract architectural floorplan dimensions room labels and structural elements from blueprint image"));
        docs.add(new Document(TaskCategory.MULTIMODAL,
            "process video frames recognize activity track object trajectory visual scene understanding"));

        // TRANSLATION
        docs.add(new Document(TaskCategory.TRANSLATION,
            "translate technical software documentation from English to Japanese with domain terminology accuracy"));
        docs.add(new Document(TaskCategory.TRANSLATION,
            "localize mobile application interface strings into Spanish French German and Mandarin Chinese"));
        docs.add(new Document(TaskCategory.TRANSLATION,
            "translate classical Arabic poetry into idiomatic English preserving poetic meter and metaphor"));
        docs.add(new Document(TaskCategory.TRANSLATION,
            "translate legal patent documents between German and English maintaining rigorous legal terminology"));
        docs.add(new Document(TaskCategory.TRANSLATION,
            "provide real-time bidirectional translation for multilingual customer support chat"));

        // GENERAL
        docs.add(new Document(TaskCategory.GENERAL,
            "explain in simple words how nuclear fusion works to a ten-year-old child"));
        docs.add(new Document(TaskCategory.GENERAL,
            "suggest a 3-day travel itinerary for visiting Tokyo Japan with cultural and food highlights"));
        docs.add(new Document(TaskCategory.GENERAL,
            "what are the health benefits of green tea versus black tea and how should it be brewed"));
        docs.add(new Document(TaskCategory.GENERAL,
            "brainstorm names for a sustainable eco-friendly coffee subscription company"));
        docs.add(new Document(TaskCategory.GENERAL,
            "casual conversation tell me an amusing riddle or fun trivia fact about dinosaurs"));

        // AGENTIC
        docs.add(new Document(TaskCategory.AGENTIC,
            "build an autonomous agent that navigates web pages extracts flight prices and books reservations"));
        docs.add(new Document(TaskCategory.AGENTIC,
            "multi-step workflow calling weather API database query slack notification error retry logic"));
        docs.add(new Document(TaskCategory.AGENTIC,
            "autonomous coding agent that reads repository runs tests fixes failing assertions and commits to git"));
        docs.add(new Document(TaskCategory.AGENTIC,
            "orchestrate multi-agent team with planner researcher reviewer using function tools and external APIs"));
        docs.add(new Document(TaskCategory.AGENTIC,
            "automated tool use invoking bash shell commands file system inspection and web search synthesis"));

        return Collections.unmodifiableList(docs);
    }
}
