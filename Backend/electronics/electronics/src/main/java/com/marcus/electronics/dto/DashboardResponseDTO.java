package com.marcus.electronics.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponseDTO {
    // KPI Cards
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long newCustomers;
    private Long lowStockSkus;

    // Charts Data
    private Map<String, BigDecimal> revenueByDay; // Chart Doanh thu 7 ngày qua
    private Map<String, Long> orderStatusRatio; // Chart Tỷ lệ trạng thái đơn hàng

    // Lists
    private List<OrderResponseDTO> recentOrders;
    private List<TopProductDTO> topProducts;

    @Data
    @Builder
    public static class TopProductDTO {
        private String name;
        private String imageUrl;
        private Long soldCount;
        private BigDecimal revenue;
    }
}