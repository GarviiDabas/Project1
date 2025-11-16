// src/main/java/com/restaurant/restaurantorderingsystem/controller/CartController.java
package com.restaurant.restaurantorderingsystem.controller;

import com.restaurant.restaurantorderingsystem.entity.MenuItem;
import com.restaurant.restaurantorderingsystem.entity.OrderEntity;
import com.restaurant.restaurantorderingsystem.model.CartItem;
import com.restaurant.restaurantorderingsystem.repository.MenuItemRepository;
import com.restaurant.restaurantorderingsystem.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 🔹 View cart
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", total);
        return "cart";
    }

    // 🔹 Add to cart
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("itemId") Long itemId,
                            @RequestParam("quantity") int quantity,
                            @RequestParam(value = "category", required = false) String category,
                            @RequestParam(value = "admin", defaultValue = "false") boolean isAdmin,
                            HttpSession session) {

        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        boolean found = false;
        for (CartItem c : cart) {
            if (c.getMenuItem().getId().equals(item.getId())) {
                c.setQuantity(c.getQuantity() + quantity);
                found = true;
                break;
            }
        
        }

        if (!found) {
            cart.add(new CartItem(item, quantity));
        }

        session.setAttribute("cart", cart);

        // 🔄 Redirect with category/admin info preserved
        String redirectUrl = "redirect:/menu";
        if (category != null && !category.isEmpty()) {
            redirectUrl += "?category=" + category + "&admin=" + isAdmin;
        } else if (isAdmin) {
            redirectUrl += "?admin=true";
        }

        return redirectUrl;
    }

    // 🔹 Remove item from cart
    @GetMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getMenuItem().getId().equals(id));
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // 🔹 Checkout
    @PostMapping("/cart/checkout")
    public String checkout(@RequestParam String customerName, HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("message", "Cart is empty!");
            return "redirect:/cart";
        }

        List<MenuItem> menuItems = new ArrayList<>();
        double total = 0.0;
        for (CartItem item : cart) {
            for (int i = 0; i < item.getQuantity(); i++) {
                menuItems.add(item.getMenuItem());
                total += item.getMenuItem().getPrice();
            }
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerName(customerName);
        order.setMenuItems(menuItems);
        order.setTotalPrice(total);
        order.setTimestamp(LocalDateTime.now());

        orderRepository.save(order);
        session.removeAttribute("cart");

        model.addAttribute("order", order);
        return "order-confirmation";
    }
}
