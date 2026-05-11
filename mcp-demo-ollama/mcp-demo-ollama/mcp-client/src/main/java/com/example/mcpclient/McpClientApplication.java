package com.example.mcpclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI MCP Client — Product Catalogue Chat Interface
 *
 * Prerequisites:
 *   1. mcp-server must be running on port 8080
 *   2. ANTHROPIC_API_KEY env var must be set (or edit application.yml for OpenAI)
 *
 * Once running:
 *   - Chat UI:    http://localhost:8081
 *   - Chat API:   POST http://localhost:8081/chat
 *   - Health:     GET  http://localhost:8081/health
 */
@SpringBootApplication
public class McpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }
}
