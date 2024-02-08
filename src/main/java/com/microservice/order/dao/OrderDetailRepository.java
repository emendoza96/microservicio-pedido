package com.microservice.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.order.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    
}
