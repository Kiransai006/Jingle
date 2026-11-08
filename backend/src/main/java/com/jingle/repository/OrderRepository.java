package com.jingle.repository;

import com.jingle.model.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<FoodOrder, Long> {

    List<FoodOrder> findAllByOrderByCreatedAtDesc();
}
