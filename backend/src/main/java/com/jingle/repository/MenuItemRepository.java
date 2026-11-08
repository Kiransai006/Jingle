package com.jingle.repository;

import com.jingle.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantIdOrderByBestsellerDescNameAsc(Long restaurantId);
}
