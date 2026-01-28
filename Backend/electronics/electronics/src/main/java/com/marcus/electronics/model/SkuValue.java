package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skuValue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;

    @ManyToOne
    @JoinColumn(name = "option_value_id", nullable = false)
    private OptionValue optionValue;
}