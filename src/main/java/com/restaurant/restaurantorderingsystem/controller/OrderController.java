package com.restaurant.restaurantorderingsystem.controller;

import com.restaurant.restaurantorderingsystem.entity.MenuItem;
import com.restaurant.restaurantorderingsystem.entity.OrderEntity;
import com.restaurant.restaurantorderingsystem.repository.MenuItemRepository;
import com.restaurant.restaurantorderingsystem.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ✅ Redirect /order to /order/new
    @GetMapping("/order")
    public String redirectToNewOrderForm() {
        return "redirect:/order/new";
    }

    // ✅ Public: Show order form
    @GetMapping("/order/new")
    public String showOrderForm(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAll());
        model.addAttribute("orderEntity", new OrderEntity());
        return "order-form";
    }

    // ✅ Public: Handle order submission
    @PostMapping("/order")
    public String placeOrder(@RequestParam List<Long> menuItemIds,
                             @RequestParam String customerName,
                             Model model) {

        List<MenuItem> selectedItems = menuItemRepository.findAllById(menuItemIds);
        double totalPrice = selectedItems.stream().mapToDouble(MenuItem::getPrice).sum();

        OrderEntity order = new OrderEntity(customerName, totalPrice, selectedItems);

        orderRepository.save(order);
        model.addAttribute("order", order);
        return "order-confirmation";
    }

    // ✅ Admin Only: View all orders
    @GetMapping("/orders")
    public String viewAllOrders(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/login";
        }

        List<OrderEntity> allOrders = orderRepository.findAll();
        model.addAttribute("orders", allOrders);
        return "order-list";
    }

    // ✅ Admin Only: Delete an order
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) {
            return "redirect:/login";
        }

        orderRepository.deleteById(id);
        return "redirect:/orders";
    }
}
