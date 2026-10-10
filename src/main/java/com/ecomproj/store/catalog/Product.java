package com.ecomproj.store.catalog;

import java.math.BigDecimal;

public record Product(
        long id,
        String name,
        String category,
        String tagline,
        String description,
        BigDecimal price,
        BigDecimal originalPrice,
        String image,
        String badge,
        double rating,
        int reviewCount) {
}