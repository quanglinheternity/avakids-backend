package com.example.avakids_backend.controller.Dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.Dashboard.DashboardOverviewResponse;
import com.example.avakids_backend.service.Dashboard.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewResponse> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }
}
