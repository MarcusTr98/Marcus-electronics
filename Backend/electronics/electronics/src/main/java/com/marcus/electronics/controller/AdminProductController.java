package com.marcus.electronics.controller;

import com.marcus.electronics.dto.ProductListResponseDTO;
import com.marcus.electronics.dto.ProductRequestDTO;
import com.marcus.electronics.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<List<ProductListResponseDTO>> getAllProductsForAdmin() {
        return ResponseEntity.ok(productService.getAllProductsForAdmin());
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        // Dùng @Valid để kích hoạt các ràng buộc @NotBlank, @Min trong DTO
        productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Thêm sản phẩm thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        productService.updateProduct(id, dto);
        return ResponseEntity.ok("Cập nhật sản phẩm thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restoreProduct(@PathVariable Long id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok().build();
    }
}