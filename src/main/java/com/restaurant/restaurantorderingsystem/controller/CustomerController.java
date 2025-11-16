package com.restaurant.restaurantorderingsystem.controller;

import com.restaurant.restaurantorderingsystem.entity.MenuItem;
import com.restaurant.restaurantorderingsystem.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping("/menu")
    public String showCustomerMenu(@RequestParam(value = "category", required = false) String category,
                                   Model model,
                                   HttpSession session) {

        List<MenuItem> items = (category != null && !category.equalsIgnoreCase("All"))
                ? menuItemRepository.findByCategoryIgnoreCase(category)
                : menuItemRepository.findAll();

        model.addAttribute("menuItems", items);
        model.addAttribute("selectedCategory", category != null ? category : "All");

        Object isAdmin = session.getAttribute("isAdmin");
        model.addAttribute("isAdmin", isAdmin != null && (Boolean) isAdmin);

        @SuppressWarnings("unchecked")
        List<com.restaurant.restaurantorderingsystem.model.CartItem> cart =
                (List<com.restaurant.restaurantorderingsystem.model.CartItem>) session.getAttribute("cart");

        int cartCount = (cart != null) ? cart.stream().mapToInt(com.restaurant.restaurantorderingsystem.model.CartItem::getQuantity).sum() : 0;
        model.addAttribute("cartCount", cartCount);

        return "menu";
    }

}
