package com.microservice.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.order.domain.Construction;

public interface ConstructionRepository extends JpaRepository<Construction, Integer> {
    
}
