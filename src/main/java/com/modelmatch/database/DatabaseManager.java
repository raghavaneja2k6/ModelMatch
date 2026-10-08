package com.modelmatch.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages SQLite JDBC connection and relational schema lifecycle.
 * Fully compliant with relational persistence standards.
 * Supports persistent SQLite file storage with automatic directory creation,
 * and resilient in-memory fallback if file system access is constrained.
 */
public class DatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;

    private final String jdbcUrl;
    private Connection connection;

    private DatabaseManager() {
        this.jdbcUrl = determineJdbcUrl();
        initDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private String determineJdbcUrl() {
        try {
            // Create data directory in current working directory
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    LOGGER.info("Created data directory for SQLite persistence: " + dataDir.getAbsolutePath());
                }
            }
            File dbFile = new File(dataDir, "modelmatch.db");
            return "jdbc:sqlite:" + dbFile.getAbsolutePath();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize SQLite file path, falling back to memory database: {0}", e.getMessage());
            return "jdbc:sqlite::memory:";
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(jdbcUrl);
            try (Statement s = connection.createStatement()) {
                // Enable foreign keys and WAL mode for high concurrency
                s.execute("PRAGMA foreign_keys = ON;");
                if (!jdbcUrl.contains(":memory:")) {
                    s.execute("PRAGMA journal_mode = WAL;");
                }
            }
        }
        return connection;
    }

    /**
     * Initializes the normalized 4-table relational schema matching project documentation:
     * 1. models
     * 2. model_capabilities
     * 3. model_benchmarks
     * 4. recommendation_logs
     */
    private void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // 1. models (Master Catalog)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS models (
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
            """);

            // 2. model_capabilities (6D Normalized Capability Vectors)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS model_capabilities (
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
            """);

            // 3. model_benchmarks (Verified Empirical Benchmark Citations)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS model_benchmarks (
                    benchmark_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    model_id VARCHAR(50) NOT NULL,
                    metric_name VARCHAR(100) NOT NULL,
                    score_value VARCHAR(100) NOT NULL,
                    source_citation VARCHAR(255),
                    FOREIGN KEY (model_id) REFERENCES models(model_id) ON DELETE CASCADE
                );
            """);

            // 4. recommendation_logs (Audit & Telemetry)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS recommendation_logs (
                    query_id VARCHAR(64) PRIMARY KEY,
                    prompt_text TEXT,
                    detected_category VARCHAR(50),
                    top_model_id VARCHAR(50),
                    match_score REAL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """);

            LOGGER.info("Relational schema initialized successfully (SQLite JDBC).");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize relational schema: {0}", e.getMessage());
        }
    }
}
