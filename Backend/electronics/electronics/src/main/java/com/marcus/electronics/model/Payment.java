package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment") // Trong SQL Server, tên bảng là payment
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @Column(name = "paymentTime")
    private LocalDateTime paymentTime;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "transactionCode", length = 100)
    private String transactionCode;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @PrePersist
    protected void onCreate() {
        paymentTime = LocalDateTime.now();
    }
}