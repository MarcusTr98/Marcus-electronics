package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "[option]")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "name", length = 50, nullable = false)
    private String name;
}