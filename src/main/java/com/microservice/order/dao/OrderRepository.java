package com.microservice.order.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.microservice.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    public List<Order> findOrderByConstructionId(@Param("id") Integer id);

}
