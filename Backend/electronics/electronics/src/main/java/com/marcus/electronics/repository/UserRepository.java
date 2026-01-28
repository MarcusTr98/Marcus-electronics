package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user để đăng nhập
    Optional<User> findByUsername(String username);

    // Kiểm tra trùng lặp khi đăng ký
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}