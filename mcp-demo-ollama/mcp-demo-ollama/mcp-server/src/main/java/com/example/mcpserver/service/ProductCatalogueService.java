package com.example.mcpserver.service;

import com.example.mcpserver.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory product catalogue — replace with your real DB/API in production.
 *
 * Pre-loaded with 8 realistic products across 3 categories so the live demo
 * works out of the box without any external dependencies.
 */
@Service
public class ProductCatalogueService {

    private final Map<String, Product> catalogue = new LinkedHashMap<>();

    public ProductCatalogueService() {
        seed();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Find a product by its exact SKU. Returns empty if not found. */
    public Optional<Product> findBySku(String sku) {
        return Optional.ofNullable(catalogue.get(sku.toUpperCase()));
    }

    /** Search products by name or description (case-insensitive contains). */
    public List<Product> search(String query) {
        String q = query.toLowerCase();
        return catalogue.values().stream()
                .filter(p -> p.name().toLowerCase().contains(q)
                          || p.description().toLowerCase().contains(q)
                          || p.category().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /** Return all products with stock level below the given threshold. */
    public List<Product> lowStock(int threshold) {
        return catalogue.values().stream()
                .filter(p -> p.stockLevel() <= threshold)
                .sorted(Comparator.comparingInt(Product::stockLevel))
                .collect(Collectors.toList());
    }

    /** Return all products in a given category. */
    public List<Product> byCategory(String category) {
        return catalogue.values().stream()
                .filter(p -> p.category().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /** Return all distinct category names. */
    public List<String> categories() {
        return catalogue.values().stream()
                .map(Product::category)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ── Seed data ─────────────────────────────────────────────────────────────

    private void seed() {
        add("SKU-001", "Wireless Noise-Cancelling Headphones",
                "Electronics", 89.99, 42,
                "Over-ear Bluetooth 5.3 headphones, 30-hour battery, foldable design");
        add("SKU-002", "Mechanical Keyboard TKL",
                "Electronics", 129.99, 15,
                "Tenkeyless layout, Cherry MX Red switches, per-key RGB backlight");
        add("SKU-003", "4K USB-C Monitor 27\"",
                "Electronics", 349.99, 0,
                "3840x2160 IPS panel, 60Hz, USB-C 65W PD, built-in USB hub");
        add("SKU-004", "Ergonomic Office Chair",
                "Furniture", 299.99, 8,
                "Lumbar support, adjustable armrests, mesh back, 5-year warranty");
        add("SKU-005", "Standing Desk 140cm",
                "Furniture", 449.99, 3,
                "Electric height adjustment 70-120cm, dual motor, memory presets");
        add("SKU-006", "Java Programming — Complete Guide",
                "Books", 49.99, 200,
                "Covers Java 17-25 LTS including virtual threads, records, pattern matching");
        add("SKU-007", "Spring Boot in Action",
                "Books", 44.99, 150,
                "Practical guide to Spring Boot 3.x, microservices, and cloud-native Java");
        add("SKU-008", "USB-C Hub 7-in-1",
                "Electronics", 34.99, 5,
                "4K HDMI, 100W PD, 3x USB-A, SD card reader — works with all USB-C laptops");
    }

    private void add(String sku, String name, String cat, double price, int stock, String desc) {
        catalogue.put(sku, new Product(sku, name, cat, price, stock, desc));
    }
}
