package com.marcus.electronics.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RevenueReportDTO {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer totalProductsSold;
    private List<DailyRevenueDTO> dailyRevenues;

    @Data
    @Builder
    public static class DailyRevenueDTO {
        private LocalDate date;
        private Integer totalOrders;
        private Integer productsSold;
        private BigDecimal revenue;
    }
}