package com.example.mcpserver;

import com.example.mcpserver.model.Product;
import com.example.mcpserver.service.ProductCatalogueService;
import com.example.mcpserver.service.ProductCatalogueTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCatalogueToolsTest {

    private ProductCatalogueTools tools;

    @BeforeEach
    void setUp() {
        tools = new ProductCatalogueTools(new ProductCatalogueService());
    }

    @Test
    void getProductBySku_knownSku_returnsProduct() {
        Object result = tools.getProductBySku("SKU-001");
        assertThat(result).isInstanceOf(Product.class);
        Product p = (Product) result;
        assertThat(p.sku()).isEqualTo("SKU-001");
        assertThat(p.price()).isEqualTo(89.99);
        assertThat(p.stockLevel()).isEqualTo(42);
    }

    @Test
    void getProductBySku_unknownSku_returnsErrorMap() {
        Object result = tools.getProductBySku("SKU-999");
        assertThat(result).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) result).get("error").toString())
                .contains("No product found");
    }

    @Test
    void getProductBySku_caseInsensitive() {
        Object upper = tools.getProductBySku("SKU-001");
        Object lower = tools.getProductBySku("sku-001");
        assertThat(upper).isEqualTo(lower);
    }

    @Test
    void searchProducts_matchesName() {
        List<Product> results = tools.searchProducts("keyboard");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).sku()).isEqualTo("SKU-002");
    }

    @Test
    void searchProducts_matchesCategory() {
        List<Product> results = tools.searchProducts("books");
        assertThat(results).hasSize(2);
    }

    @Test
    void searchProducts_noMatch_returnsEmpty() {
        List<Product> results = tools.searchProducts("xyznotaproduct");
        assertThat(results).isEmpty();
    }

    @Test
    void getLowStockProducts_returnsOnlyBelowThreshold() {
        List<Product> results = tools.getLowStockProducts(5);
        // SKU-003 (stock=0), SKU-008 (stock=5) should be included
        assertThat(results).extracting(Product::stockLevel)
                .allSatisfy(stock -> assertThat(stock).isLessThanOrEqualTo(5));
    }

    @Test
    void getLowStockProducts_sortedAscending() {
        List<Product> results = tools.getLowStockProducts(10);
        for (int i = 0; i < results.size() - 1; i++) {
            assertThat(results.get(i).stockLevel())
                    .isLessThanOrEqualTo(results.get(i + 1).stockLevel());
        }
    }

    @Test
    void getProductsByCategory_electronics_returnsCorrectCount() {
        Object result = tools.getProductsByCategory("Electronics");
        assertThat(result).isInstanceOf(List.class);
        @SuppressWarnings("unchecked")
        List<Product> products = (List<Product>) result;
        assertThat(products).hasSize(4); // SKU-001, 002, 003, 008
    }

    @Test
    void getProductsByCategory_invalidCategory_returnsError() {
        Object result = tools.getProductsByCategory("Gadgets");
        assertThat(result).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) result).containsKey("error")).isTrue();
        assertThat(((Map<?, ?>) result).containsKey("availableCategories")).isTrue();
    }
}
