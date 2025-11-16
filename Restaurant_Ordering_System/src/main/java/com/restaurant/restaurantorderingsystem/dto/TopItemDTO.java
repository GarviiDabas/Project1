package com.restaurant.restaurantorderingsystem.dto;

public class TopItemDTO {
    private String itemName;
    private long quantitySold;

    public TopItemDTO(String itemName, long quantitySold) {
        this.itemName = itemName;
        this.quantitySold = quantitySold;
    }

    public String getItemName() {
        return itemName;
    }

    public long getQuantitySold() {
        return quantitySold;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantitySold(long quantitySold) {
        this.quantitySold = quantitySold;
    }
}
