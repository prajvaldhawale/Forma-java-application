package com.ecomproj.store.catalog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCatalogTest {
    private final ProductCatalog catalog = new ProductCatalog();

    @Test
    void filtersProductsByCategoryAndSearch() {
        var results = catalog.findProducts("Audio", "speaker", "featured");

        assertEquals(1, results.size());
        assertEquals("Room Speaker", results.getFirst().name());
    }

    @Test
    void sortsByPriceAscending() {
        var results = catalog.findProducts("All", "", "price-asc");

        assertEquals("Noise-Cancel Buds", results.getFirst().name());
        assertTrue(results.getFirst().price().compareTo(results.getLast().price()) < 0);
    }
}