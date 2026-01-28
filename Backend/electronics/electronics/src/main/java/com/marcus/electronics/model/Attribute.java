package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attribute")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 50)
    private String code;
}