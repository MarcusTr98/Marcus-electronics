package com.marcus.electronics.controller;

import com.marcus.electronics.dto.CustomerResponseDTO;
import com.marcus.electronics.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminCustomerController {

    private final CustomerService customerService;

    @GetMapping("")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        if (!body.containsKey("active")) {
            return ResponseEntity.badRequest().body("Thiếu trường active");
        }
        Boolean isActive = Boolean.valueOf(body.get("active").toString());
        customerService.toggleCustomerStatus(id, isActive);
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }
}