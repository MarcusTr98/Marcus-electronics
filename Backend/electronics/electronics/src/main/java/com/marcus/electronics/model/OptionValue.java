package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "optionValue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optionId", nullable = false)
    private Option option;

    @Column(name = "value", nullable = false)
    private String value;
}