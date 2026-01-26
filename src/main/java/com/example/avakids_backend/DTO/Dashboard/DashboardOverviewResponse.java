package com.example.avakids_backend.DTO.Dashboard;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewResponse {
    private OrderStats orderStats;
    private RevenueStats revenueStats;
    private UserStats userStats;
    private OrderCompletionRate completionRate;
    private Long pendingOrders;
    private List<RevenueByDate> revenueChart;
    private List<OrderByStatus> orderStatusChart;
    private List<TopProduct> topProducts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStats {
        private Long today;
        private Long thisWeek;
        private Long thisMonth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueStats {
        private BigDecimal today;
        private BigDecimal thisMonth;
        private BigDecimal allTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long today;
        private Long thisMonth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderCompletionRate {
        private Long completed;
        private Long cancelled;
        private Double completionRate;
        private Double cancellationRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByDate {
        private Date date;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderByStatus {
        private String status;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private Long productId;
        private String productName;
        private String imageUrl;
        private Long totalSold;
        private BigDecimal revenue;
    }
}
