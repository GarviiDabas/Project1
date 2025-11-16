package com.restaurant.restaurantorderingsystem.dto;

public class MonthlySalesDTO {
    private String monthLabel;
    private long orderCount;
    private double totalRevenue;

    public MonthlySalesDTO(String monthLabel, long orderCount, double totalRevenue) {
        this.monthLabel = monthLabel;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    // ✅ Required Getters
    public String getMonthLabel() {
        return monthLabel;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
