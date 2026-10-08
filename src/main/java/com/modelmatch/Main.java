package com.modelmatch;

import com.modelmatch.server.ModelMatchServer;

/**
 * Main application entry point for ModelMatch.
 */
public class Main {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        try {
            ModelMatchServer server = new ModelMatchServer(port);
            server.start();

            // Keep main thread alive
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down ModelMatch Server...");
                server.stop();
            }));

            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Fatal error starting ModelMatch: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
