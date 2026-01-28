package com.marcus.electronics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marcus.electronics.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy danh sách đơn hàng của 1 user (Lịch sử mua hàng)
    List<Order> findByUserId(Long userId);

    // Tìm đơn hàng theo mã đơn (nếu sau này bạn thêm cột order_code)
    // Hoặc tìm theo Số điện thoại
    List<Order> findByPhoneNumber(String phoneNumber);
}