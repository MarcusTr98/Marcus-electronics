package com.marcus.electronics.service;

import com.marcus.electronics.dto.DashboardResponseDTO;
import com.marcus.electronics.dto.OrderResponseDTO;
import com.marcus.electronics.model.Order;
import com.marcus.electronics.model.OrderDetail;
import com.marcus.electronics.model.User;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final OrderRepository orderRepository;
        private final UserRepository userRepository;
        private final SkuRepository skuRepository;
        private final OrderDetailRepository orderDetailRepository;

        // HÀM HỖ TRỢ: Tính phần trăm tăng trưởng
        private Double calculateTrend(double current, double previous) {
                if (previous == 0) {
                        return current > 0 ? 100.0 : 0.0;
                }
                double trend = ((current - previous) / previous) * 100.0;
                return Math.round(trend * 10.0) / 10.0;
        }

        @Transactional(readOnly = true)
        public DashboardResponseDTO getDashboardData() {
                List<Order> allOrders = orderRepository.findAll();
                List<User> allUsers = userRepository.findAll();

                // XÁC ĐỊNH MỐC THỜI GIAN TUYỆT ĐỐI CHÍNH XÁC
                LocalDateTime now = LocalDateTime.now();
                // clear nano-seconds để tránh sai lệch milliseconds khi filter
                LocalDateTime startOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                                .withNano(0);
                LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);

                // ==========================================
                // 1. TÍNH KPI CARDS & TREND (Chỉ tính dữ liệu của THÁNG NÀY)
                // ==========================================

                // --- A. DOANH THU ---
                BigDecimal thisMonthRev = allOrders.stream()
                                .filter(o -> "DELIVERED".equals(o.getStatus())
                                                && !o.getOrderDate().isBefore(startOfThisMonth))
                                .map(Order::getTotalMoney)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal lastMonthRev = allOrders.stream()
                                .filter(o -> "DELIVERED".equals(o.getStatus())
                                                && !o.getOrderDate().isBefore(startOfLastMonth)
                                                && o.getOrderDate().isBefore(startOfThisMonth))
                                .map(Order::getTotalMoney)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Double revenueTrend = calculateTrend(thisMonthRev.doubleValue(), lastMonthRev.doubleValue());

                // --- B. ĐƠN HÀNG ---
                Integer thisMonthOrders = (int) allOrders.stream()
                                .filter(o -> !o.getOrderDate().isBefore(startOfThisMonth))
                                .count();

                long lastMonthOrders = allOrders.stream()
                                .filter(o -> !o.getOrderDate().isBefore(startOfLastMonth)
                                                && o.getOrderDate().isBefore(startOfThisMonth))
                                .count();

                Double orderTrend = calculateTrend((double) thisMonthOrders, (double) lastMonthOrders);

                // --- C. KHÁCH HÀNG MỚI ---
                List<User> normalUsers = allUsers.stream()
                                .filter(u -> !u.getRole().getName().contains("ADMIN"))
                                .collect(Collectors.toList());

                Integer thisMonthCust = (int) normalUsers.stream()
                                .filter(u -> u.getCreatedAt() != null && !u.getCreatedAt().isBefore(startOfThisMonth))
                                .count();

                long lastMonthCust = normalUsers.stream()
                                .filter(u -> u.getCreatedAt() != null && !u.getCreatedAt().isBefore(startOfLastMonth)
                                                && u.getCreatedAt().isBefore(startOfThisMonth))
                                .count();

                Double customerTrend = calculateTrend((double) thisMonthCust, (double) lastMonthCust);

                // --- D. CẢNH BÁO TỒN KHO (Tồn kho là con số realtime, không cần trend) ---
                Integer lowStockSkus = (int) skuRepository.findAll().stream()
                                .filter(s -> s.getStock() < 10 && s.getIsActive())
                                .count();

                // ==========================================
                // 2. BIỂU ĐỒ TRẠNG THÁI
                // ==========================================
                Map<String, Integer> statusRatio = allOrders.stream()
                                .collect(Collectors.groupingBy(
                                                Order::getStatus,
                                                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

                // ==========================================
                // 3. BIỂU ĐỒ DOANH THU & SỐ ĐƠN (7 NGÀY)
                // ==========================================
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

                Map<String, BigDecimal> revenueByDay = new LinkedHashMap<>();
                Map<String, Integer> ordersByDay = new LinkedHashMap<>();
                Map<String, Integer> completionByDay = new LinkedHashMap<>();
                Map<String, Integer> cancellationByDay = new LinkedHashMap<>();

                for (int i = 6; i >= 0; i--) {
                        String dateKey = today.minusDays(i).format(formatter);
                        revenueByDay.put(dateKey, BigDecimal.ZERO);
                        ordersByDay.put(dateKey, 0);
                        completionByDay.put(dateKey, 0);
                        cancellationByDay.put(dateKey, 0);
                }

                allOrders.stream()
                                .filter(o -> !o.getOrderDate().toLocalDate().isBefore(today.minusDays(6)))
                                .forEach(o -> {
                                        String dateKey = o.getOrderDate().toLocalDate().format(formatter);
                                        ordersByDay.put(dateKey, ordersByDay.get(dateKey) + 1);

                                        if ("DELIVERED".equals(o.getStatus())) {
                                                revenueByDay.put(dateKey,
                                                                revenueByDay.get(dateKey).add(o.getTotalMoney()));
                                                completionByDay.put(dateKey, completionByDay.get(dateKey) + 1);
                                        } else if ("CANCELLED".equals(o.getStatus())) {
                                                cancellationByDay.put(dateKey, cancellationByDay.get(dateKey) + 1);
                                        }
                                });

                // ==========================================
                // 4. TOP SẢN PHẨM BÁN CHẠY
                // ==========================================
                List<OrderDetail> allDetails = orderDetailRepository.findAll();
                Map<String, DashboardResponseDTO.TopProductDTO> topProducts = allDetails.stream()
                                .filter(d -> d.getOrder() != null && "DELIVERED".equals(d.getOrder().getStatus()))
                                .collect(Collectors.groupingBy(
                                                d -> d.getSku().getProduct().getName(),
                                                Collectors.collectingAndThen(Collectors.toList(), list -> {
                                                        BigDecimal rev = list.stream().map(OrderDetail::getTotalMoney)
                                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                                        int qty = list.stream()
                                                                        .mapToInt(OrderDetail::getNumberOfProducts)
                                                                        .sum();
                                                        return new DashboardResponseDTO.TopProductDTO(rev, qty);
                                                })))
                                .entrySet().stream()
                                .sorted((e1, e2) -> e2.getValue().getRevenue().compareTo(e1.getValue().getRevenue()))
                                .limit(5)
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                                LinkedHashMap::new));

                // ==========================================
                // 5. ĐƠN HÀNG MỚI NHẤT
                // ==========================================
                List<OrderResponseDTO> recentOrders = orderRepository
                                .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderDate")))
                                .stream().map(o -> OrderResponseDTO.builder()
                                                .id(o.getId())
                                                .fullName(o.getFullName())
                                                .totalMoney(o.getTotalMoney())
                                                .status(o.getStatus())
                                                .orderDate(o.getOrderDate())
                                                .phoneNumber(o.getPhoneNumber())
                                                .build())
                                .collect(Collectors.toList());

                return DashboardResponseDTO.builder()
                                .totalRevenue(thisMonthRev) // Ép DTO lấy dữ liệu của THÁNG NÀY
                                .revenueTrend(revenueTrend)
                                .totalOrders(thisMonthOrders) // Ép DTO lấy dữ liệu của THÁNG NÀY
                                .orderTrend(orderTrend)
                                .newCustomers(thisMonthCust) // Ép DTO lấy dữ liệu của THÁNG NÀY
                                .customerTrend(customerTrend)
                                .lowStockSkus(lowStockSkus)
                                .revenueByDay(revenueByDay)
                                .ordersByDay(ordersByDay)
                                .orderStatusRatio(statusRatio)
                                .recentOrders(recentOrders)
                                .topProducts(topProducts)
                                .completionByDay(completionByDay)
                                .cancellationByDay(cancellationByDay)
                                .build();
        }
}