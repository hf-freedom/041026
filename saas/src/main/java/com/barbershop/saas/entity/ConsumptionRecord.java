package com.barbershop.saas.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consumption_records")
public class ConsumptionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long merchantId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long barberId;

    @Column
    private Long hairstyleTypeId;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal originalAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal actualAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal barberCommission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(length = 500)
    private String hairstyleOptions;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum PaymentMethod {
        BALANCE,
        CASH,
        CARD,
        OTHER
    }
}
