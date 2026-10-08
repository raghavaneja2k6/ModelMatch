package com.modelmatch.server;

import com.modelmatch.classifier.TfIdfClassifier;
import com.modelmatch.controller.ApiHandler;
import com.modelmatch.engine.ExplainabilityService;
import com.modelmatch.engine.RecommendationEngine;
import com.modelmatch.repository.ModelDatabase;
import com.modelmatch.service.GeminiService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Embedded HTTP Server for ModelMatch.
 * Serves the ChatGPT replica frontend and the REST API.
 */
public class ModelMatchServer {

    private static final Logger LOGGER = Logger.getLogger(ModelMatchServer.class.getName());
    private final int port;
    private HttpServer server;

    public ModelMatchServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ModelDatabase modelDatabase = new ModelDatabase();
        TfIdfClassifier tfIdfClassifier = new TfIdfClassifier();
        RecommendationEngine recommendationEngine = new RecommendationEngine(modelDatabase);
        ExplainabilityService explainabilityService = new ExplainabilityService();
        GeminiService geminiService = new GeminiService();

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor()); // Java 21 Virtual Threads!

        // API Handler
        ApiHandler apiHandler = new ApiHandler(
            modelDatabase, tfIdfClassifier, recommendationEngine, explainabilityService, geminiService
        );
        server.createContext("/api", apiHandler);

        // Static Asset Handler for ChatGPT Replica UI
        server.createContext("/", new StaticFileHandler());

        server.start();
        LOGGER.info("=================================================================");
        LOGGER.info("🚀 ModelMatch Server started successfully on http://localhost:" + port);
        LOGGER.info("⚡ Powered by Java 21, TF-IDF Classifier & Google Gemini 3.5 Flash");
        LOGGER.info("🎨 ChatGPT Replica UI live at: http://localhost:" + port);
        LOGGER.info("=================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    static class StaticFileHandler implements HttpHandler {
        private static final String STATIC_DIR_OVERRIDE = "src/main/resources/static";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            // Prevent path traversal
            if (path.contains("..")) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            byte[] content = loadFile(path);
            if (content == null) {
                // Fallback to index.html for SPA routing if requested
                content = loadFile("/index.html");
                if (content == null) {
                    String msg = "404 Not Found: Static asset " + path + " missing";
                    exchange.sendResponseHeaders(404, msg.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(msg.getBytes());
                    }
                    return;
                }
            }

            String mime = getMimeType(path);
            exchange.getResponseHeaders().set("Content-Type", mime);

            // Lighthouse optimization: cache static images and assets
            if (path.endsWith(".png") || path.endsWith(".webp") || path.endsWith(".svg") || path.endsWith(".ico") || path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                exchange.getResponseHeaders().set("Cache-Control", "public, max-age=604800, immutable");
            } else if (path.endsWith(".css") || path.endsWith(".js")) {
                exchange.getResponseHeaders().set("Cache-Control", "public, max-age=86400");
            } else {
                exchange.getResponseHeaders().set("Cache-Control", "no-cache, must-revalidate");
            }

            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }

        private byte[] loadFile(String resourcePath) {
            // 1. Try file system relative path (ideal for development live edits)
            try {
                Path localPath = Paths.get(STATIC_DIR_OVERRIDE + resourcePath);
                if (Files.exists(localPath) && !Files.isDirectory(localPath)) {
                    return Files.readAllBytes(localPath);
                }
            } catch (Exception ignored) {}

            // 2. Try classpath
            try (InputStream is = getClass().getResourceAsStream("/static" + resourcePath)) {
                if (is != null) {
                    return is.readAllBytes();
                }
            } catch (Exception ignored) {}

            return null;
        }

        private String getMimeType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".json")) return "application/json; charset=UTF-8";
            if (path.endsWith(".svg")) return "image/svg+xml";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".webp")) return "image/webp";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            if (path.endsWith(".ico")) return "image/x-icon";
            return "text/plain; charset=UTF-8";
        }
    }
}
