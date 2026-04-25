package com.marcus.electronics.service;

import com.marcus.electronics.dto.RevenueReportDTO;
import com.marcus.electronics.model.Order;
import com.marcus.electronics.model.OrderDetail;
import com.marcus.electronics.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public RevenueReportDTO getRevenueReport(LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .filter(o -> !o.getOrderDate().isBefore(start) && !o.getOrderDate().isAfter(end))
                .collect(Collectors.toList());

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalProductsSold = 0;

        Map<LocalDate, RevenueReportDTO.DailyRevenueDTO> dailyMap = new TreeMap<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dailyMap.put(current, RevenueReportDTO.DailyRevenueDTO.builder()
                    .date(current)
                    .totalOrders(0)
                    .productsSold(0)
                    .revenue(BigDecimal.ZERO)
                    .build());
            current = current.plusDays(1);
        }

        for (Order order : orders) {
            LocalDate dateKey = order.getOrderDate().toLocalDate();
            RevenueReportDTO.DailyRevenueDTO dailyStat = dailyMap.get(dateKey);

            int itemsInOrder = 0;

            dailyStat.setTotalOrders(dailyStat.getTotalOrders() + 1);
            dailyStat.setRevenue(dailyStat.getRevenue().add(order.getTotalMoney()));

            totalRevenue = totalRevenue.add(order.getTotalMoney());
        }

        List<RevenueReportDTO.DailyRevenueDTO> dailyRevenues = new ArrayList<>(dailyMap.values());
        Collections.reverse(dailyRevenues);

        return RevenueReportDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(orders.size())
                .totalProductsSold(totalProductsSold) // Tạm set 0, bạn có thể loop OrderDetail để cộng thêm
                .dailyRevenues(dailyRevenues)
                .build();
    }
}