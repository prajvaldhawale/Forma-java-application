package com.ecomproj.store.cart;

import com.ecomproj.store.catalog.Product;
import com.ecomproj.store.catalog.ProductCatalog;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@SessionScope
public class ShoppingCart {
    private final Map<Long, Integer> quantities = new LinkedHashMap<>();

    public void add(long productId, ProductCatalog catalog) {
        requireProduct(productId, catalog);
        quantities.merge(productId, 1, Integer::sum);
    }

    public void update(long productId, int quantity) {
        if (quantity <= 0) {
            quantities.remove(productId);
        } else if (quantities.containsKey(productId)) {
            quantities.put(productId, quantity);
        }
    }

    public void remove(long productId) {
        quantities.remove(productId);
    }

    public List<CartLine> lines(ProductCatalog catalog) {
        return quantities.entrySet().stream()
                .map(entry -> {
                    Product product = requireProduct(entry.getKey(), catalog);
                    return new CartLine(product, entry.getValue(), product.price().multiply(BigDecimal.valueOf(entry.getValue())));
                })
                .toList();
    }

    public BigDecimal subtotal(ProductCatalog catalog) {
        return lines(catalog).stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int itemCount() {
        return quantities.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isEmpty() {
        return quantities.isEmpty();
    }

    public void clear() {
        quantities.clear();
    }

    private Product requireProduct(long productId, ProductCatalog catalog) {
        return catalog.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + productId));
    }
}