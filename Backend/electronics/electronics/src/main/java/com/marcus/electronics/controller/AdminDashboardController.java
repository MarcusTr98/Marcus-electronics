package com.marcus.electronics.controller;

import com.marcus.electronics.dto.DashboardResponseDTO;
import com.marcus.electronics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("")
    public ResponseEntity<DashboardResponseDTO> getDashboardData() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
}