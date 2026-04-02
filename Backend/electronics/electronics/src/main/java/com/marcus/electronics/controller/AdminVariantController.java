package com.marcus.electronics.controller;

import com.marcus.electronics.dto.OptionRequestDTO;
import com.marcus.electronics.dto.OptionResponseDTO;
import com.marcus.electronics.dto.SkuRequestDTO;
import com.marcus.electronics.dto.SkuUpdateRequestDTO;
import com.marcus.electronics.service.SkuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products/{productId}")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminVariantController {

    private final SkuService skuService;

    // 1. API Tạo Option (VD: Thêm "Màu sắc" cho iPhone 15)
    @PostMapping("/options")
    public ResponseEntity<OptionResponseDTO> createOption(
            @PathVariable Long productId,
            @Valid @RequestBody OptionRequestDTO request) {

        // Gọi Service và hứng data trả về
        OptionResponseDTO response = skuService.createOptionWithOptionsValues(
                productId,
                request.getName(),
                request.getValues());

        // Trả data này về cho Vue.js (Kèm status 201 Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. API Tạo SKU (Sau khi đã có OptionValue ID)
    @PostMapping("/skus")
    public ResponseEntity<?> createSku(
            @PathVariable Long productId,
            @Valid @RequestBody SkuRequestDTO request) {

        skuService.createSkuForProduct(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo mã SKU thành công");
    }

    // 3. Xóa mềm SKU
    @DeleteMapping("/skus/{skuId}")
    public ResponseEntity<?> deleteSku(
            @PathVariable Long productId,
            @PathVariable Long skuId) {
        // Có thể validate thêm xem skuId này có thuộc productId này không để bảo mật
        skuService.deleteSku(skuId);
        return ResponseEntity.ok("Đã xóa SKU");
    }

    // 4. API Lấy chi tiết toàn bộ SKU của 1 sản phẩm
    @GetMapping("/skus")
    public ResponseEntity<?> getProductVariants(@PathVariable Long productId) {
        return ResponseEntity.ok(skuService.getProductVariantsDetail(productId));
    }

    // 5. API Khóa/Mở khóa SKU (Soft Delete)
    @PatchMapping("/skus/{skuId}/status")
    public ResponseEntity<?> toggleSkuStatus(
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @RequestBody Map<String, Object> body) {

        if (!body.containsKey("active")) {
            return ResponseEntity.badRequest().body("Thiếu trạng thái active");
        }
        Boolean isActive = Boolean.valueOf(body.get("active").toString());

        skuService.toggleSkuStatus(skuId, isActive);
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }

    // 6. API Cập nhật thông tin cơ bản của SKU (Giá, Kho, Ảnh)
    @PutMapping("/skus/{skuId}")
    public ResponseEntity<?> updateSkuInfo(
            @PathVariable Long productId,
            @PathVariable Long skuId,
            @Valid @RequestBody SkuUpdateRequestDTO request) {

        skuService.updateSkuInfo(skuId, request);
        return ResponseEntity.ok("Cập nhật biến thể thành công");
    }

}