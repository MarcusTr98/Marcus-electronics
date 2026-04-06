package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productAttributeValue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attributeId", nullable = false)
    private Attribute attribute;

    @Column(name = "value", length = 255, nullable = false)
    private String value;

    @Column(name = "valueNumber", precision = 18, scale = 2)
    private BigDecimal valueNumber;
}