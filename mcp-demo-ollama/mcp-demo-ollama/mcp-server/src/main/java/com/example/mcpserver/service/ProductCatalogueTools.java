package com.example.mcpserver.service;

import com.example.mcpserver.model.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * MCP Tool definitions for the Product Catalogue.
 *
 * Each @Tool method becomes a tool the LLM can discover and call.
 *
 * IMPORTANT: The description in @Tool is the signal the LLM uses to decide
 * WHEN and HOW to call your tool. Be specific and action-oriented.
 * "Handles products" → tool gets ignored.
 * "Look up a product by its SKU identifier and return its current price and stock level" → works perfectly.
 *
 * Note on annotations:
 *   Spring AI 1.0.x  → @Tool + @ToolParam  (used here — widest compatibility)
 *   Spring AI 1.1+   → @McpTool + @McpToolParam  (newer annotation-scanner approach)
 * Both produce identical OOXML tool schemas. Swap if you are on 1.1+.
 */
@Service
public class ProductCatalogueTools {

    private final ProductCatalogueService catalogue;

    public ProductCatalogueTools(ProductCatalogueService catalogue) {
        this.catalogue = catalogue;
    }

    // ── Tool 1: Lookup by SKU ─────────────────────────────────────────────────

    @Tool(
        name = "getProductBySku",
        description = """
            Look up a single product by its SKU identifier.
            Returns the product name, category, current price in USD, stock level, and description.
            Use this when the user provides a specific SKU code (e.g. SKU-001).
            Returns an error message if the SKU is not found.
            """
    )
    public Object getProductBySku(
            @ToolParam(description = "The SKU identifier, e.g. SKU-001. Case-insensitive.")
            String sku) {
        return catalogue.findBySku(sku)
                .<Object>map(p -> p)
                .orElse(Map.of("error", "No product found with SKU: " + sku));
    }

    // ── Tool 2: Search ────────────────────────────────────────────────────────

    @Tool(
        name = "searchProducts",
        description = """
            Search the product catalogue by keyword.
            Searches product names, descriptions, and category names.
            Returns a list of matching products with prices and stock levels.
            Use this when the user asks about a type of product without providing a SKU,
            e.g. "do you have any keyboards?" or "show me books about Java".
            """
    )
    public List<Product> searchProducts(
            @ToolParam(description = "Search keyword, e.g. 'keyboard', 'monitor', 'Java', 'chair'")
            String query) {
        List<Product> results = catalogue.search(query);
        if (results.isEmpty()) {
            // Return an informative empty result rather than null
            return List.of();
        }
        return results;
    }

    // ── Tool 3: Low stock alert ───────────────────────────────────────────────

    @Tool(
        name = "getLowStockProducts",
        description = """
            Return all products whose stock level is at or below the given threshold.
            Sorted by stock level ascending (most critical first).
            Use this when the user asks about inventory levels, stock alerts,
            or which products need restocking.
            Default threshold is 10 if not specified.
            """
    )
    public List<Product> getLowStockProducts(
            @ToolParam(description = "Maximum stock level to include. Products with stock <= this value are returned. Default: 10")
            int threshold) {
        return catalogue.lowStock(threshold);
    }

    // ── Tool 4: Browse by category ────────────────────────────────────────────

    @Tool(
        name = "getProductsByCategory",
        description = """
            Return all products in a specific category.
            Available categories: Electronics, Furniture, Books.
            Use this when the user asks to see all products in a category,
            e.g. "show me all electronics" or "what furniture do you sell?".
            """
    )
    public Object getProductsByCategory(
            @ToolParam(description = "Category name — one of: Electronics, Furniture, Books")
            String category) {
        List<String> validCategories = catalogue.categories();
        List<Product> products = catalogue.byCategory(category);
        if (products.isEmpty()) {
            return Map.of(
                "error", "No products found in category: " + category,
                "availableCategories", validCategories
            );
        }
        return products;
    }
}
