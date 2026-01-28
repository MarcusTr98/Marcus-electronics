package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orderDetail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "skuId", nullable = false)
    private Sku sku;

    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price; // Giá tại thời điểm mua

    @Column(name = "numberOfProducts", nullable = false)
    private Integer numberOfProducts;

    @Column(name = "totalMoney", nullable = false)
    private BigDecimal totalMoney;
}