package com.example.mcpserver.config;

import com.example.mcpserver.service.ProductCatalogueTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the ProductCatalogueTools with the MCP server.
 *
 * Spring AI scans the ToolCallbackProvider bean and:
 *  1. Generates JSON Schema from each @Tool method's parameter types
 *  2. Registers the tools with the MCP server at startup
 *  3. Routes incoming tool-call requests back to the method
 *
 * Note for Spring AI 1.1+: if you switch to @McpTool annotations and enable
 * spring.ai.mcp.server.annotation-scanner.enabled=true, you can remove this
 * config class entirely — auto-discovery handles registration.
 */
@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider productCatalogueToolCallbacks(
            ProductCatalogueTools tools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tools)
                .build();
    }
}
