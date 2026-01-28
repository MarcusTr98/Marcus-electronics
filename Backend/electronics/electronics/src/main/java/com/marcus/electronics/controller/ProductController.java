package com.marcus.electronics.controller;

import com.marcus.electronics.dto.ProductDetailResponseDTO;
import com.marcus.electronics.dto.ProductListResponseDTO;
import com.marcus.electronics.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;

    // GET /api/v1/products
    @GetMapping("")
    public ResponseEntity<List<ProductListResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/v1/products/iphone-15-pro-max
    @GetMapping("/{slug}")
    public ResponseEntity<?> getProductDetail(@PathVariable String slug) {
        try {
            ProductDetailResponseDTO product = productService.getProductBySlug(slug);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}