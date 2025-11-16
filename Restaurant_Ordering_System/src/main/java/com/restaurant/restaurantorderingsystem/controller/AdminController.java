package com.restaurant.restaurantorderingsystem.controller;

import com.restaurant.restaurantorderingsystem.dto.*;
import com.restaurant.restaurantorderingsystem.entity.MenuItem;
import com.restaurant.restaurantorderingsystem.entity.OrderEntity;
import com.restaurant.restaurantorderingsystem.repository.MenuItemRepository;
import com.restaurant.restaurantorderingsystem.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    // 🔹 Redirect root to admin dashboard
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    // 🔹 Admin Dashboard with stats and charts
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        long totalOrders = orderRepository.count();

        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(OrderEntity::getTotalPrice)
                .sum();

        List<TopItemDTO> topItems = orderRepository.findTopSellingItems()
                .stream().limit(3).collect(Collectors.toList());

        List<String> itemNames = topItems.stream()
                .map(TopItemDTO::getItemName)
                .collect(Collectors.toList());

        List<Long> itemCounts = topItems.stream()
                .map(TopItemDTO::getQuantitySold)
                .collect(Collectors.toList());

        List<DailySalesDTO> dayWiseSales = orderRepository.getRawDayWiseSales().stream()
                .map(row -> new DailySalesDTO(
                        ((java.sql.Date) row[0]).toLocalDate(),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).doubleValue()
                )).collect(Collectors.toList());

        List<WeeklySalesDTO> weekWiseSales = orderRepository.getRawWeekWiseSales().stream()
                .map(row -> new WeeklySalesDTO(
                        "Week " + row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).doubleValue()
                )).collect(Collectors.toList());

        List<MonthlySalesDTO> monthWiseSales = orderRepository.getRawMonthWiseSales().stream()
                .map(row -> new MonthlySalesDTO(
                        YearMonth.parse(row[0].toString()).format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).doubleValue()
                )).collect(Collectors.toList());

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("topItems", topItems);
        model.addAttribute("itemNames", itemNames);
        model.addAttribute("itemCounts", itemCounts);
        model.addAttribute("dailySales", dayWiseSales);
        model.addAttribute("weeklySales", weekWiseSales);
        model.addAttribute("monthlySales", monthWiseSales);
        model.addAttribute("orders", orderRepository.findAll());

        return "admin-dashboard";
    }

    // 🔹 Redirect old path
    @GetMapping("/menu/manage")
    public String handleLegacyMenuManageRedirect() {
        return "redirect:/admin/menu";
    }

}
