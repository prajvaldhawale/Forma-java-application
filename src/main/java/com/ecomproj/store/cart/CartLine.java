package com.ecomproj.store.cart;

import com.ecomproj.store.catalog.Product;

import java.math.BigDecimal;

public record CartLine(Product product, int quantity, BigDecimal lineTotal) {
}