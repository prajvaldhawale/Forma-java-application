package com.ecomproj.store.catalog;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ProductCatalog {
    private final List<Product> products = List.of(
            new Product(1, "Studio Headphones", "Audio", "Hear every detail.",
                    "Balanced wireless sound, soft memory-foam cushions, and a 40-hour battery.",
                    new BigDecimal("249.00"), new BigDecimal("299.00"),
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=85",
                    "BESTSELLER", 4.9, 284),
            new Product(2, "Everyday Phone", "Phones", "A little more capable.",
                    "A vivid OLED display, all-day battery, and a camera ready for wherever the day goes.",
                    new BigDecimal("599.00"), new BigDecimal("649.00"),
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=85",
                    "SAVE $50", 4.8, 196),
            new Product(3, "Aero 14 Laptop", "Computing", "Make room for big ideas.",
                    "A light aluminum laptop with a crisp display and enough power for the everyday.",
                    new BigDecimal("1199.00"), new BigDecimal("1299.00"),
                    "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=85",
                    "NEW", 4.7, 153),
            new Product(4, "Studio Monitor", "TV & Home", "Your room, reimagined.",
                    "Rich 4K picture, clear dialogue, and a slim design made for movie nights.",
                    new BigDecimal("899.00"), new BigDecimal("999.00"),
                    "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?auto=format&fit=crop&w=900&q=85",
                    "SAVE $100", 4.8, 118),
            new Product(5, "Tempo Watch", "Wearables", "Keep your day in rhythm.",
                    "Thoughtful health insights, activity tracking, and a comfortable all-day fit.",
                    new BigDecimal("279.00"), new BigDecimal("319.00"),
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=85",
                    "POPULAR", 4.6, 92),
            new Product(6, "Pocket Camera", "Cameras", "Bring the moment closer.",
                    "A compact mirrorless camera with a bright lens and intuitive everyday controls.",
                    new BigDecimal("749.00"), new BigDecimal("799.00"),
                    "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=85",
                    "SAVE $50", 4.7, 84),
            new Product(7, "Room Speaker", "Audio", "Fill the room, not your shelf.",
                    "Warm, room-filling sound in a compact wireless speaker with simple pairing.",
                    new BigDecimal("149.00"), new BigDecimal("169.00"),
                    "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=85",
                    "JUST IN", 4.5, 67),
            new Product(8, "Noise-Cancel Buds", "Audio", "A quieter kind of focus.",
                    "Pocket-sized wireless earbuds with adaptive noise cancellation and a snug fit.",
                    new BigDecimal("129.00"), new BigDecimal("149.00"),
                    "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?auto=format&fit=crop&w=900&q=85",
                    "SAVE $20", 4.6, 211));

    public List<Product> findProducts(String category, String query, String sort) {
        Stream<Product> result = products.stream();
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("All")) {
            result = result.filter(product -> product.category().equalsIgnoreCase(category));
        }
        if (query != null && !query.isBlank()) {
            String search = query.trim().toLowerCase(Locale.ROOT);
            result = result.filter(product -> (product.name() + " " + product.category() + " " + product.tagline())
                    .toLowerCase(Locale.ROOT).contains(search));
        }
        Comparator<Product> comparator = switch (sort == null ? "featured" : sort) {
            case "price-asc" -> Comparator.comparing(Product::price);
            case "price-desc" -> Comparator.comparing(Product::price).reversed();
            case "rating" -> Comparator.comparingDouble(Product::rating).reversed();
            default -> null;
        };
        return comparator == null ? result.toList() : result.sorted(comparator).toList();
    }

    public List<String> categories() {
        return Stream.concat(Stream.of("All"), products.stream()
                        .map(Product::category)
                        .distinct()
                        .sorted())
                .toList();
    }

    public Optional<Product> findById(long id) {
        return products.stream().filter(product -> product.id() == id).findFirst();
    }
}