package com.microservice.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.order.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

}
