package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "optionValue")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(name = "value", length = 50, nullable = false)
    private String value;
}