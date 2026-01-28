package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.OptionValue;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {
}