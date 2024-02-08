package com.microservice.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
}
