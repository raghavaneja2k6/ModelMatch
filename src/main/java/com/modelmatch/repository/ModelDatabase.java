package com.modelmatch.repository;

import com.modelmatch.database.ModelDAO;
import com.modelmatch.database.ModelSeeder;
import com.modelmatch.model.ModelEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Model repository backed by the SQLite relational database (ModelDAO).
 * Loads model entities dynamically from the database at startup and supports
 * dynamic insertion, lookups, and recommendation logging.
 */
public class ModelDatabase {

    private static final Logger LOGGER = Logger.getLogger(ModelDatabase.class.getName());

    private final ModelDAO modelDAO;
    private final Map<String, ModelEntity> cache = new ConcurrentHashMap<>();

    public ModelDatabase() {
        this.modelDAO = new ModelDAO();
        initializeCatalog();
    }

    /**
     * Initializes the catalog by verifying the relational database and populating the cache.
     */
    private void initializeCatalog() {
        try {
            // Seed database if empty
            ModelSeeder.seedIfNecessary(modelDAO);

            // Load all models dynamically from relational database
            List<ModelEntity> dbModels = modelDAO.findAll();
            if (dbModels.isEmpty()) {
                // Fallback to in-memory seed if SQLite cannot write
                LOGGER.warning("Relational database query returned 0 models. Falling back to in-memory seed catalog.");
                dbModels = ModelSeeder.getSeedCatalog();
            }

            for (ModelEntity m : dbModels) {
                cache.put(m.getId().toLowerCase(), m);
            }
            LOGGER.info("ModelDatabase initialized successfully with " + cache.size() + " models from database.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing ModelDatabase from database, using seed fallback: {0}", e.getMessage());
            for (ModelEntity m : ModelSeeder.getSeedCatalog()) {
                cache.put(m.getId().toLowerCase(), m);
            }
        }
    }

    /**
     * Registers a new model into the database and active cache.
     */
    public void register(ModelEntity model) {
        if (model == null || model.getId() == null) return;
        cache.put(model.getId().toLowerCase(), model);
        try {
            modelDAO.insertModel(model);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to persist model {0} to database: {1}", new Object[]{model.getId(), e.getMessage()});
        }
    }

    /**
     * Returns all models currently loaded in the catalog.
     */
    public List<ModelEntity> getAllModels() {
        return new ArrayList<>(cache.values());
    }

    /**
     * Retrieves a single model by ID.
     */
    public Optional<ModelEntity> getModelById(String id) {
        if (id == null) return Optional.empty();
        String normalizedId = id.toLowerCase().trim();
        ModelEntity cached = cache.get(normalizedId);
        if (cached != null) {
            return Optional.of(cached);
        }
        // Fallback check directly in DAO
        Optional<ModelEntity> fromDb = modelDAO.findById(normalizedId);
        fromDb.ifPresent(m -> cache.put(m.getId().toLowerCase(), m));
        return fromDb;
    }

    /**
     * Searches models by name, provider, or description.
     */
    public List<ModelEntity> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllModels();
        }
        String q = query.toLowerCase();
        return cache.values().stream()
            .filter(m -> m.getName().toLowerCase().contains(q)
                || m.getProvider().toLowerCase().contains(q)
                || m.getDescription().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    /**
     * Logs recommendation execution to the relational recommendation_logs table.
     */
    public void logRecommendation(String queryId, String prompt, String category, String topModelId, double matchScore) {
        try {
            modelDAO.logRecommendation(queryId, prompt, category, topModelId, matchScore);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Failed to write recommendation log: {0}", e.getMessage());
        }
    }

    public int getModelCount() {
        return cache.size();
    }
}
