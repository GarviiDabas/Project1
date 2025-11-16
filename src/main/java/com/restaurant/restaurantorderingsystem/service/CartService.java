package com.restaurant.restaurantorderingsystem.service;

import com.restaurant.restaurantorderingsystem.entity.MenuItem;
import com.restaurant.restaurantorderingsystem.model.CartItem;
import com.restaurant.restaurantorderingsystem.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final MenuItemRepository menuItemRepository;
    private final List<CartItem> cartItems = new ArrayList<>();

    public CartService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public void addToCart(Long itemId, int quantity) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        for (CartItem ci : cartItems) {
            if (ci.getMenuItem().getId().equals(itemId)) {
                ci.setQuantity(ci.getQuantity() + quantity);
                return;
            }
        }

        cartItems.add(new CartItem(item, quantity));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeItem(Long itemId) {
        cartItems.removeIf(ci -> ci.getMenuItem().getId().equals(itemId));
    }

    public double getTotal() {
        return cartItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public void clearCart() {
        cartItems.clear();
    }
}
