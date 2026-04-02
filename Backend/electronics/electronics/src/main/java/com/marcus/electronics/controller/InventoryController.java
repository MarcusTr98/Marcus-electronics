package com.marcus.electronics.controller;

import com.marcus.electronics.dto.InventoryResponseDTO;
import com.marcus.electronics.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InventoryController {

    private final SkuService skuService;

    @GetMapping("")
    public ResponseEntity<List<InventoryResponseDTO>> getInventory() {
        return ResponseEntity.ok(skuService.getInventoryReport());
    }

    @PatchMapping("/{skuId}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long skuId, @RequestBody Map<String, Integer> body) {
        if (!body.containsKey("stock")) {
            return ResponseEntity.badRequest().body("Thiếu trường stock");
        }
        Integer stock = body.get("stock");
        skuService.updateStock(skuId, stock);
        return ResponseEntity.ok("Cập nhật kho thành công");
    }
}