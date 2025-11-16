package com.restaurant.restaurantorderingsystem.repository;

import com.restaurant.restaurantorderingsystem.entity.OrderEntity;
import com.restaurant.restaurantorderingsystem.dto.DailySalesDTO;
import com.restaurant.restaurantorderingsystem.dto.TopItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	@Query("SELECT new com.restaurant.restaurantorderingsystem.dto.TopItemDTO(m.name, COUNT(m)) " +
	           "FROM OrderEntity o JOIN o.menuItems m " +
	           "GROUP BY m.name " +
	           "ORDER BY COUNT(m) DESC")
	    List<TopItemDTO> findTopSellingItems();
	
	// Daily Sales 
	@Query("SELECT DATE(o.timestamp), COUNT(o), SUM(o.totalPrice) FROM OrderEntity o GROUP BY DATE(o.timestamp) ORDER BY DATE(o.timestamp)")
	List<Object[]> getRawDayWiseSales();

	// Weekly Sales
	@Query("SELECT FUNCTION('YEARWEEK', o.timestamp), COUNT(o), SUM(o.totalPrice) FROM OrderEntity o GROUP BY FUNCTION('YEARWEEK', o.timestamp) ORDER BY FUNCTION('YEARWEEK', o.timestamp)")
	List<Object[]> getRawWeekWiseSales();

	// Monthly Sales
	@Query("SELECT FUNCTION('DATE_FORMAT', o.timestamp, '%Y-%m'), COUNT(o), SUM(o.totalPrice) FROM OrderEntity o GROUP BY FUNCTION('DATE_FORMAT', o.timestamp, '%Y-%m') ORDER BY FUNCTION('DATE_FORMAT', o.timestamp, '%Y-%m')")
	List<Object[]> getRawMonthWiseSales();

}
