package com.marcus.electronics.service;

import com.marcus.electronics.dto.DashboardResponseDTO;
import com.marcus.electronics.dto.OrderResponseDTO;
import com.marcus.electronics.model.Order;
import com.marcus.electronics.model.Sku;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SkuRepository skuRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboardData() {
        List<Order> allOrders = orderRepository.findAll();

        // 1. TÍNH KPI CARDS
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .map(Order::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalOrders = (long) allOrders.size();

        // Đếm KH (Loại trừ ADMIN)
        Long newCustomers = userRepository.findAll().stream()
                .filter(u -> !u.getRole().getName().contains("ADMIN"))
                .count();

        // Đếm SKU sắp hết hàng (< 10)
        Long lowStockSkus = skuRepository.findAll().stream()
                .filter(s -> s.getStock() < 10 && s.getIsActive())
                .count();

        // 2. BIỂU ĐỒ TRẠNG THÁI ĐƠN HÀNG (Doughnut Chart)
        Map<String, Long> statusRatio = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        // 3. BIỂU ĐỒ DOANH THU 7 NGÀY GẦN NHẤT (Line Chart)
        LocalDate today = LocalDate.now();
        Map<String, BigDecimal> revenueByDay = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        // Khởi tạo 7 ngày với mốc 0 VNĐ để tránh biểu đồ bị đứt đoạn
        for (int i = 6; i >= 0; i--) {
            revenueByDay.put(today.minusDays(i).format(formatter), BigDecimal.ZERO);
        }

        // Lấp đầy dữ liệu thật
        allOrders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus())
                        && o.getOrderDate().toLocalDate().isAfter(today.minusDays(7)))
                .forEach(o -> {
                    String dateKey = o.getOrderDate().toLocalDate().format(formatter);
                    revenueByDay.put(dateKey, revenueByDay.get(dateKey).add(o.getTotalMoney()));
                });

        // 4. LẤY 5 ĐƠN HÀNG MỚI NHẤT
        List<OrderResponseDTO> recentOrders = orderRepository
                .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderDate")))
                .stream().map(o -> OrderResponseDTO.builder()
                        .id(o.getId())
                        .fullName(o.getFullName())
                        .totalMoney(o.getTotalMoney())
                        .status(o.getStatus())
                        .build())
                .collect(Collectors.toList());

        // Đóng gói trả về
        return DashboardResponseDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .newCustomers(newCustomers)
                .lowStockSkus(lowStockSkus)
                .revenueByDay(revenueByDay)
                .orderStatusRatio(statusRatio)
                .recentOrders(recentOrders)
                // Chỗ này bạn có thể tự viết query Top Product sau, tạm trả mảng rỗng để không
                // lỗi
                .topProducts(new ArrayList<>())
                .build();
    }
}