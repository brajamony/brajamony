package com.example.mcpserver.model;

/**
 * Represents a product in the catalogue.
 * This is the return type of our MCP tools — Spring AI serialises it to JSON
 * and the LLM reads the field names + values to build its natural-language answer.
 */
public record Product(
        String sku,
        String name,
        String category,
        double price,
        int stockLevel,
        String description
) {
    /** Convenience method used in tool responses */
    public boolean isInStock() {
        return stockLevel > 0;
    }
}
