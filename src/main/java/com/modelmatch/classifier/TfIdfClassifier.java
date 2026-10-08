package com.modelmatch.classifier;

import com.modelmatch.model.TaskCategory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Supervised TF-IDF + Cosine Similarity Text Classifier implemented in pure Java.
 * Computes TF-IDF vectors for documents and categorizes user requests by comparing
 * cosine similarity with category centroids.
 */
public class TfIdfClassifier {

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
        "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being",
        "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't",
        "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during",
        "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have",
        "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers",
        "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm",
        "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's",
        "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off",
        "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out",
        "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should",
        "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs",
        "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll",
        "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up",
        "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't",
        "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's",
        "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll",
        "you're", "you've", "your", "yours", "yourself", "yourselves", "want", "need", "like"
    ));

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

    private final List<String> vocabulary = new ArrayList<>();
    private final Map<String, Integer> termIndexMap = new HashMap<>();
    private final Map<String, Double> idfMap = new HashMap<>();
    private final Map<TaskCategory, double[]> categoryCentroids = new EnumMap<>(TaskCategory.class);

    public TfIdfClassifier() {
        train(TaskCorpus.getTrainingDocuments());
    }

    /**
     * Trains the classifier on annotated documents.
     */
    public synchronized void train(List<TaskCorpus.Document> documents) {
        vocabulary.clear();
        termIndexMap.clear();
        idfMap.clear();
        categoryCentroids.clear();

        // 1. Build vocabulary and document frequency
        Map<String, Integer> docFreq = new HashMap<>();
        List<List<String>> tokenizedDocs = new ArrayList<>();

        for (TaskCorpus.Document doc : documents) {
            List<String> tokens = tokenize(doc.getText());
            tokenizedDocs.add(tokens);
            Set<String> uniqueTokens = new HashSet<>(tokens);
            for (String t : uniqueTokens) {
                docFreq.put(t, docFreq.getOrDefault(t, 0) + 1);
            }
        }

        int N = documents.size();
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            String term = entry.getKey();
            int df = entry.getValue();
            termIndexMap.put(term, vocabulary.size());
            vocabulary.add(term);
            // Smoothed IDF: ln(1 + N / (1 + df))
            double idf = Math.log(1.0 + ((double) N / (1.0 + df)));
            idfMap.put(term, idf);
        }

        // 2. Compute vectors for each category by aggregating documents
        Map<TaskCategory, List<double[]>> categoryVectors = new EnumMap<>(TaskCategory.class);
        for (int i = 0; i < documents.size(); i++) {
            TaskCategory cat = documents.get(i).getCategory();
            double[] vec = computeTfIdfVector(tokenizedDocs.get(i));
            categoryVectors.computeIfAbsent(cat, k -> new ArrayList<>()).add(vec);
        }

        // 3. Compute centroid for each category
        int dim = vocabulary.size();
        for (TaskCategory cat : TaskCategory.values()) {
            List<double[]> list = categoryVectors.get(cat);
            double[] centroid = new double[dim];
            if (list != null && !list.isEmpty()) {
                for (double[] v : list) {
                    for (int j = 0; j < dim; j++) {
                        centroid[j] += v[j];
                    }
                }
                for (int j = 0; j < dim; j++) {
                    centroid[j] /= list.size();
                }
                // Normalize centroid vector to unit length
                normalizeVector(centroid);
            }
            categoryCentroids.put(cat, centroid);
        }
    }

    /**
     * Classifies a user query string. Returns the top category and probability distribution.
     */
    public ClassificationResult classify(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ClassificationResult(TaskCategory.GENERAL, 50.0, Collections.emptyMap());
        }

        List<String> tokens = tokenize(query);
        double[] queryVec = computeTfIdfVector(tokens);
        normalizeVector(queryVec);

        Map<String, Double> similarityMap = new LinkedHashMap<>();
        TaskCategory bestCat = TaskCategory.GENERAL;
        double maxSim = -1.0;

        for (TaskCategory cat : TaskCategory.values()) {
            double[] centroid = categoryCentroids.get(cat);
            double sim = (centroid != null) ? cosineSimilarity(queryVec, centroid) : 0.0;
            // Floor similarity at 0
            sim = Math.max(0.0, sim);
            similarityMap.put(cat.getDisplayName(), sim);
            if (sim > maxSim) {
                maxSim = sim;
                bestCat = cat;
            }
        }

        // Convert similarities to confidence percentage using Softmax or proportional scaling
        double sum = 0.0;
        for (double s : similarityMap.values()) {
            sum += Math.exp(s * 4.0); // Temperature scaling
        }

        Map<String, Double> normalizedConfidence = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : similarityMap.entrySet()) {
            double conf = (Math.exp(entry.getValue() * 4.0) / sum) * 100.0;
            normalizedConfidence.put(entry.getKey(), Math.round(conf * 10.0) / 10.0);
        }

        double bestConfidence = normalizedConfidence.getOrDefault(bestCat.getDisplayName(), 75.0);
        return new ClassificationResult(bestCat, bestConfidence, normalizedConfidence);
    }

    private double[] computeTfIdfVector(List<String> tokens) {
        double[] vec = new double[vocabulary.size()];
        if (tokens.isEmpty()) return vec;

        Map<String, Integer> counts = new HashMap<>();
        for (String t : tokens) {
            counts.put(t, counts.getOrDefault(t, 0) + 1);
        }

        int totalWords = tokens.size();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String term = entry.getKey();
            Integer idx = termIndexMap.get(term);
            if (idx != null) {
                double tf = (double) entry.getValue() / totalWords;
                double idf = idfMap.getOrDefault(term, 1.0);
                vec[idx] = tf * idf;
            }
        }
        return vec;
    }

    private void normalizeVector(double[] v) {
        double norm = 0.0;
        for (double val : v) {
            norm += val * val;
        }
        norm = Math.sqrt(norm);
        if (norm > 1e-9) {
            for (int i = 0; i < v.length; i++) {
                v[i] /= norm;
            }
        }
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        if (denominator < 1e-9) return 0.0;
        return dot / denominator;
    }

    public List<String> tokenize(String text) {
        if (text == null) return Collections.emptyList();
        List<String> tokens = new ArrayList<>();
        var matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group();
            if (word.length() > 1 && !STOPWORDS.contains(word)) {
                tokens.add(stem(word));
            }
        }
        return tokens;
    }

    /**
     * Lightweight suffix stemmer for English suffixes to improve recall.
     */
    private String stem(String word) {
        if (word.endsWith("ing") && word.length() > 5) {
            return word.substring(0, word.length() - 3);
        }
        if (word.endsWith("ed") && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("tion") && word.length() > 6) {
            return word.substring(0, word.length() - 4);
        }
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 3) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    public static class ClassificationResult {
        private final TaskCategory category;
        private final double confidence;
        private final Map<String, Double> distribution;

        public ClassificationResult(TaskCategory category, double confidence, Map<String, Double> distribution) {
            this.category = category;
            this.confidence = confidence;
            this.distribution = distribution;
        }

        public TaskCategory getCategory() { return category; }
        public double getConfidence() { return confidence; }
        public Map<String, Double> getDistribution() { return distribution; }
    }
}
