package com.marcus.electronics.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "[order]")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "note")
    private String note;

    @Column(name = "orderDate")
    private LocalDateTime orderDate;

    @Column(name = "status")
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "totalMoney", nullable = false)
    private BigDecimal totalMoney;

    @Column(name = "shippingMethod")
    private String shippingMethod;

    @Column(name = "paymentMethod")
    @Builder.Default
    private String paymentMethod = "COD";

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
}