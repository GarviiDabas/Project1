package com.restaurant.restaurantorderingsystem.repository;

import com.restaurant.restaurantorderingsystem.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // 🔍 Enables filtering by category (case-insensitive)
    List<MenuItem> findByCategoryIgnoreCase(String category);
    
}
