package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sku")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(name = "skuCode", unique = true, nullable = false)
    private String skuCode;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Column(name = "isActive")
    @Builder.Default
    private Boolean isActive = true;
}