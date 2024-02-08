package com.microservice.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.order.domain.OrderState;

public interface OrderStateRepository extends JpaRepository<OrderState, Integer> {
    
}
