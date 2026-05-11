package com.example.mcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI MCP Server — Product Catalogue Demo
 *
 * Start this first, then start mcp-client in a separate terminal.
 *
 * Once running:
 *   - MCP endpoint: http://localhost:8080/mcp
 *   - Any MCP client (Claude Desktop, your client app) can connect to this URL
 *   - Logs will show "Tool registered: getProductBySku" etc. on startup
 */
@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }
}
