package com.restaurant.restaurantorderingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToMany
    @JoinTable(
        name = "orders_items",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "items_id")
    )
    private List<MenuItem> menuItems;

    public OrderEntity() {}

    public OrderEntity(String customerName, double totalPrice, List<MenuItem> menuItems) {
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.timestamp = LocalDateTime.now();
        this.menuItems = menuItems;
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }
}
