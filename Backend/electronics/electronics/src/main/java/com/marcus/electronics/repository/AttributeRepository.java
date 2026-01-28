package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.Attribute;

import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
    // Tìm thuộc tính theo mã code (vd: tìm 'ram')
    Optional<Attribute> findByCode(String code);
}