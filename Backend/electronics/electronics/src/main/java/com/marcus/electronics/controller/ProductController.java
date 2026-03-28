package com.marcus.electronics.controller;

import com.marcus.electronics.dto.ProductListResponseDTO;
import com.marcus.electronics.model.Product;
import com.marcus.electronics.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;

    // GET /api/v1/product
    @GetMapping("")
    public ResponseEntity<List<ProductListResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/v1/product/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với ID: " + id);
        }
    }

    // GET /api/v1/product/slug/iphone-15-pro-max
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getProductBySlug(@PathVariable String slug) {
        try {
            return ResponseEntity.ok(productService.getProductBySlug(slug));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}