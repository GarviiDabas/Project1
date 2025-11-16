package com.restaurant.restaurantorderingsystem.dto;

import java.time.LocalDate;

public class DailySalesDTO {
    private LocalDate dateLabel;
    private long orderCount;
    private double totalRevenue;

    public DailySalesDTO(LocalDate dateLabel, long orderCount, double totalRevenue) {
        this.dateLabel = dateLabel;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    public String getDateLabelFormatted() {
        return dateLabel != null
            ? dateLabel.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))
            : "N/A";
    }

    public long getOrderCount() {
        return orderCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
