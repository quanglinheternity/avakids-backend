package com.example.avakids_backend.service.Dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Dashboard.*;
import com.example.avakids_backend.repository.Order.OrderRepository;
import com.example.avakids_backend.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DashboardOverviewResponse getOverview() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        LocalDateTime weekStart = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                .atStartOfDay();

        LocalDateTime monthStart =
                LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();

        // Order Stats
        DashboardOverviewResponse.OrderStats orderStats = DashboardOverviewResponse.OrderStats.builder()
                .today(orderRepository.countOrdersByDateRange(todayStart, todayEnd))
                .thisWeek(orderRepository.countOrdersByDateRange(weekStart, now))
                .thisMonth(orderRepository.countOrdersByDateRange(monthStart, now))
                .build();

        // Revenue Stats
        DashboardOverviewResponse.RevenueStats revenueStats = DashboardOverviewResponse.RevenueStats.builder()
                .today(orderRepository.getTotalRevenue(todayStart, todayEnd))
                .thisMonth(orderRepository.getTotalRevenue(monthStart, now))
                .allTime(orderRepository.getTotalRevenue(LocalDateTime.of(2000, 1, 1, 0, 0), now))
                .build();

        // User Stats
        DashboardOverviewResponse.UserStats userStats = DashboardOverviewResponse.UserStats.builder()
                .today(userRepository.countNewUsers(todayStart, todayEnd))
                .thisMonth(userRepository.countNewUsers(monthStart, now))
                .build();

        // Completion Rate
        DashboardOverviewResponse.OrderCompletionRate completionRate = orderRepository.getCompletionRate();

        // Pending Orders
        Long pendingOrders = orderRepository.countPendingOrders();

        // Charts
        var revenueChart = orderRepository.getRevenueByDate(30);
        var orderStatusChart = orderRepository.getOrdersByStatus();
        var topProducts = orderRepository.getTopProducts(5);

        return DashboardOverviewResponse.builder()
                .orderStats(orderStats)
                .revenueStats(revenueStats)
                .userStats(userStats)
                .completionRate(completionRate)
                .pendingOrders(pendingOrders)
                .revenueChart(revenueChart)
                .orderStatusChart(orderStatusChart)
                .topProducts(topProducts)
                .build();
    }
}
