package com.railsetu.controller;

import com.railsetu.dto.DashboardMetricsResponse;
import com.railsetu.service.RevenueAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'FARE_ADMIN')")
public class AdminRevenueAnalyticsController {

    private final RevenueAnalyticsService revenueAnalyticsService;

    public AdminRevenueAnalyticsController(RevenueAnalyticsService revenueAnalyticsService) {
        this.revenueAnalyticsService = revenueAnalyticsService;
    }

    /**
     * Section 28: Advanced Operations Dashboard Metrics
     */
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics() {
        return ResponseEntity.ok(revenueAnalyticsService.getDashboardMetrics());
    }

    /**
     * Section 23 & 24: Revenue & Train Profitability View
     */
    @GetMapping("/revenue/overview")
    public ResponseEntity<DashboardMetricsResponse> getRevenueOverview() {
        return ResponseEntity.ok(revenueAnalyticsService.getDashboardMetrics());
    }
}
