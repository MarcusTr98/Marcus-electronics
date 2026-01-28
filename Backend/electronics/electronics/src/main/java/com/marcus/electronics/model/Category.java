package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "parentId")
    private Category parent;

    @Column(name = "isActive")
    @Builder.Default
    private Boolean isActive = true;
}