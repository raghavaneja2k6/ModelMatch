package com.modelmatch.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modelmatch.model.ModelEntity;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for LLM models and recommendations using PreparedStatements.
 * Implements clean JDBC persistence layer for academic evaluation.
 */
public class ModelDAO {

    private static final Logger LOGGER = Logger.getLogger(ModelDAO.class.getName());
    private final DatabaseManager dbManager;
    private final Gson gson;
    private final Type stringListType = new TypeToken<List<String>>() {}.getType();

    public ModelDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.gson = new Gson();
    }

    /**
     * Retrieves all models by performing a relational JOIN between models and model_capabilities.
     */
    public List<ModelEntity> findAll() {
        List<ModelEntity> list = new ArrayList<>();
        String sql = """
            SELECT m.model_id, m.name, m.provider, m.badge, m.category,
                   m.context_window_tokens, m.cost_input_per_million, m.cost_output_per_million,
                   m.speed_tokens_per_sec, m.license_type, m.benchmark_source, m.description,
                   m.strengths_json, m.weaknesses_json, m.direct_execution_supported, m.source_reference,
                   c.reasoning_score, c.coding_score, c.context_score, c.multimodal_score,
                   c.speed_score, c.cost_score, c.overall_score
            FROM models m
            JOIN model_capabilities c ON m.model_id = c.model_id
            ORDER BY c.overall_score DESC;
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToModel(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error fetching models: {0}", e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves a single model by ID.
     */
    public Optional<ModelEntity> findById(String id) {
        if (id == null) return Optional.empty();
        String sql = """
            SELECT m.model_id, m.name, m.provider, m.badge, m.category,
                   m.context_window_tokens, m.cost_input_per_million, m.cost_output_per_million,
                   m.speed_tokens_per_sec, m.license_type, m.benchmark_source, m.description,
                   m.strengths_json, m.weaknesses_json, m.direct_execution_supported, m.source_reference,
                   c.reasoning_score, c.coding_score, c.context_score, c.multimodal_score,
                   c.speed_score, c.cost_score, c.overall_score
            FROM models m
            JOIN model_capabilities c ON m.model_id = c.model_id
            WHERE LOWER(m.model_id) = LOWER(?);
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToModel(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error finding model by id: {0}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Persists a model and its capability vector across models and model_capabilities tables.
     */
    public void insertModel(ModelEntity model) {
        String insertModelSql = """
            INSERT INTO models (
                model_id, name, provider, badge, category,
                context_window_tokens, cost_input_per_million, cost_output_per_million,
                speed_tokens_per_sec, license_type, benchmark_source, description,
                strengths_json, weaknesses_json, direct_execution_supported, source_reference
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(model_id) DO UPDATE SET
                name = excluded.name,
                provider = excluded.provider,
                badge = excluded.badge,
                context_window_tokens = excluded.context_window_tokens,
                cost_input_per_million = excluded.cost_input_per_million,
                cost_output_per_million = excluded.cost_output_per_million,
                speed_tokens_per_sec = excluded.speed_tokens_per_sec,
                license_type = excluded.license_type,
                benchmark_source = excluded.benchmark_source,
                description = excluded.description,
                strengths_json = excluded.strengths_json,
                weaknesses_json = excluded.weaknesses_json,
                direct_execution_supported = excluded.direct_execution_supported,
                source_reference = excluded.source_reference;
        """;

        String insertCapSql = """
            INSERT INTO model_capabilities (
                model_id, reasoning_score, coding_score, context_score,
                multimodal_score, speed_score, cost_score, overall_score
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(model_id) DO UPDATE SET
                reasoning_score = excluded.reasoning_score,
                coding_score = excluded.coding_score,
                context_score = excluded.context_score,
                multimodal_score = excluded.multimodal_score,
                speed_score = excluded.speed_score,
                cost_score = excluded.cost_score,
                overall_score = excluded.overall_score;
        """;

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psModel = conn.prepareStatement(insertModelSql);
                 PreparedStatement psCap = conn.prepareStatement(insertCapSql)) {

                // Bind models statement
                psModel.setString(1, model.getId());
                psModel.setString(2, model.getName());
                psModel.setString(3, model.getProvider());
                psModel.setString(4, model.getBadge());
                psModel.setString(5, model.getBadge());
                psModel.setInt(6, model.getContextWindowTokens());
                psModel.setDouble(7, model.getInputCostPer1M());
                psModel.setDouble(8, model.getOutputCostPer1M());
                psModel.setInt(9, model.getSpeedTokensPerSec());
                psModel.setString(10, model.getLicense());
                psModel.setString(11, model.getBenchmarkSource());
                psModel.setString(12, model.getDescription());
                psModel.setString(13, gson.toJson(model.getStrengths()));
                psModel.setString(14, gson.toJson(model.getWeaknesses()));
                psModel.setInt(15, model.isDirectExecutionSupported() ? 1 : 0);
                psModel.setString(16, model.getSourceReference() != null ? model.getSourceReference() : "");
                psModel.executeUpdate();

                // Bind model_capabilities statement
                psCap.setString(1, model.getId());
                psCap.setDouble(2, model.getReasoningScore());
                psCap.setDouble(3, model.getCodingScore());
                psCap.setDouble(4, model.getContextScore());
                psCap.setDouble(5, model.getMultimodalScore());
                psCap.setDouble(6, model.getSpeedScore());
                psCap.setDouble(7, model.getCostScore());
                psCap.setDouble(8, model.getOverallScore());
                psCap.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error inserting model: " + model.getId(), e);
        }
    }

    /**
     * Inserts verified benchmark records into model_benchmarks table.
     */
    public void insertBenchmark(String modelId, String metricName, String scoreValue, String sourceCitation) {
        String sql = "INSERT INTO model_benchmarks (model_id, metric_name, score_value, source_citation) VALUES (?, ?, ?, ?);";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modelId);
            ps.setString(2, metricName);
            ps.setString(3, scoreValue);
            ps.setString(4, sourceCitation);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to insert benchmark for {0}: {1}", new Object[]{modelId, e.getMessage()});
        }
    }

    /**
     * Logs recommendation execution to the recommendation_logs table.
     */
    public void logRecommendation(String queryId, String prompt, String category, String topModelId, double matchScore) {
        String sql = "INSERT INTO recommendation_logs (query_id, prompt_text, detected_category, top_model_id, match_score) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, queryId != null ? queryId : UUID.randomUUID().toString());
            ps.setString(2, prompt);
            ps.setString(3, category);
            ps.setString(4, topModelId);
            ps.setDouble(5, matchScore);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.FINE, "Failed to write recommendation audit log: {0}", e.getMessage());
        }
    }

    /**
     * Returns total models count in database.
     */
    public int countModels() {
        String sql = "SELECT COUNT(*) FROM models;";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error counting models: {0}", e.getMessage());
        }
        return 0;
    }

    private ModelEntity mapResultSetToModel(ResultSet rs) throws SQLException {
        String id = rs.getString("model_id");
        String name = rs.getString("name");
        String provider = rs.getString("provider");
        String badge = rs.getString("badge");
        int contextWindow = rs.getInt("context_window_tokens");
        double inputCost = rs.getDouble("cost_input_per_million");
        double outputCost = rs.getDouble("cost_output_per_million");
        int speed = rs.getInt("speed_tokens_per_sec");
        String license = rs.getString("license_type");
        String benchmarkSource = rs.getString("benchmark_source");
        String desc = rs.getString("description");
        String strengthsJson = rs.getString("strengths_json");
        String weaknessesJson = rs.getString("weaknesses_json");
        boolean directExec = rs.getInt("direct_execution_supported") == 1;
        String sourceRef = rs.getString("source_reference");

        double r = rs.getDouble("reasoning_score");
        double c = rs.getDouble("coding_score");
        double x = rs.getDouble("context_score");
        double m = rs.getDouble("multimodal_score");
        double s = rs.getDouble("speed_score");
        double p = rs.getDouble("cost_score");
        double overall = rs.getDouble("overall_score");

        List<String> strengths = strengthsJson != null ? gson.fromJson(strengthsJson, stringListType) : new ArrayList<>();
        List<String> weaknesses = weaknessesJson != null ? gson.fromJson(weaknessesJson, stringListType) : new ArrayList<>();

        return new ModelEntity(
            id, name, provider, badge, r, c, x, m, s, p, overall,
            contextWindow, inputCost, outputCost, speed, license,
            benchmarkSource, desc, strengths, weaknesses, directExec, sourceRef
        );
    }
}
