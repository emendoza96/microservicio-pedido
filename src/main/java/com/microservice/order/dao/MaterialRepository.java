package com.microservice.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.order.domain.Material;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    
}
