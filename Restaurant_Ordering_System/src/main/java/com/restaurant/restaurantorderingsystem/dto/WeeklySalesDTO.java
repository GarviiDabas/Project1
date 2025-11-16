package com.restaurant.restaurantorderingsystem.dto;

public class WeeklySalesDTO {
    private String weekLabel;
    private long orderCount;
    private double totalRevenue;

    public WeeklySalesDTO(String weekLabel, long orderCount, double totalRevenue) {
        this.weekLabel = weekLabel;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    // ✅ Required Getters
    public String getWeekLabel() {
        return weekLabel;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
