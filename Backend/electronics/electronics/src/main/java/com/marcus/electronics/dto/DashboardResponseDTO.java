package com.marcus.electronics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponseDTO {
    private BigDecimal totalRevenue;
    private Double revenueTrend;
    private Integer totalOrders;
    private Double orderTrend;
    private Integer newCustomers;
    private Double customerTrend;
    private Integer lowStockSkus;

    private Map<String, BigDecimal> revenueByDay;
    private Map<String, Integer> ordersByDay;
    private Map<String, Integer> orderStatusRatio;

    private Map<String, TopProductDTO> topProducts;

    private Map<String, Integer> completionByDay;
    private Map<String, Integer> cancellationByDay;
    private List<OrderResponseDTO> recentOrders;

    // INNER CLASS: Đóng gói dữ liệu Top Sản phẩm
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopProductDTO {
        private BigDecimal revenue;
        private Integer quantity;
    }
}