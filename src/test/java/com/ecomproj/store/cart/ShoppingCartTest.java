package com.ecomproj.store.cart;

import com.ecomproj.store.catalog.ProductCatalog;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingCartTest {
    private final ProductCatalog catalog = new ProductCatalog();

    @Test
    void tracksQuantitiesAndSubtotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.add(1, catalog);
        cart.add(1, catalog);

        assertEquals(2, cart.itemCount());
        assertEquals(new BigDecimal("498.00"), cart.subtotal(catalog));

        cart.update(1, 1);
        assertEquals(new BigDecimal("249.00"), cart.subtotal(catalog));
    }
}