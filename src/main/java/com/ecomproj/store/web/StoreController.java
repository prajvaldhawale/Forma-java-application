package com.ecomproj.store.web;

import com.ecomproj.store.cart.ShoppingCart;
import com.ecomproj.store.catalog.ProductCatalog;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class StoreController {
    private final ProductCatalog catalog;
    private final ShoppingCart cart;

    public StoreController(ProductCatalog catalog, ShoppingCart cart) {
        this.catalog = catalog;
        this.cart = cart;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "All") String category,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "featured") String sort,
            Model model) {
        model.addAttribute("products", catalog.findProducts(category, q, sort));
        model.addAttribute("categories", catalog.categories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("query", q);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("cartCount", cart.itemCount());
        model.addAttribute("productCount", catalog.findProducts(category, q, sort).size());
        return "home";
    }

    @PostMapping("/cart/items/{id}")
    public String addToCart(@PathVariable long id) {
        cart.add(id, catalog);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("cartLines", cart.lines(catalog));
        model.addAttribute("subtotal", cart.subtotal(catalog));
        model.addAttribute("cartCount", cart.itemCount());
        return "cart";
    }

    @PostMapping("/cart/items/{id}/quantity")
    public String updateQuantity(@PathVariable long id, @RequestParam int quantity) {
        cart.update(id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/items/{id}/remove")
    public String removeFromCart(@PathVariable long id) {
        cart.remove(id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("checkoutForm", new CheckoutForm());
        addCheckoutSummary(model);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(
            @Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        if (bindingResult.hasErrors()) {
            addCheckoutSummary(model);
            return "checkout";
        }

        String orderReference = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        redirectAttributes.addFlashAttribute("orderReference", orderReference);
        redirectAttributes.addFlashAttribute("customerName", checkoutForm.getFullName());
        redirectAttributes.addFlashAttribute("customerEmail", checkoutForm.getEmail());
        redirectAttributes.addFlashAttribute("orderTotal", cart.subtotal(catalog));
        cart.clear();
        return "redirect:/order-complete";
    }

    @GetMapping("/order-complete")
    public String orderComplete(@ModelAttribute("orderReference") String orderReference) {
        return orderReference.isBlank() ? "redirect:/" : "order-complete";
    }

    private void addCheckoutSummary(Model model) {
        model.addAttribute("cartLines", cart.lines(catalog));
        model.addAttribute("subtotal", cart.subtotal(catalog));
        model.addAttribute("cartCount", cart.itemCount());
    }
}