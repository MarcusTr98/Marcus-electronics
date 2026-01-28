package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.SkuValue;

@Repository
public interface SkuValueRepository extends JpaRepository<SkuValue, Long> {
}