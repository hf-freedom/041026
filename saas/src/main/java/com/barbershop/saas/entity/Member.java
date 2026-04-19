package com.barbershop.saas.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long merchantId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberLevel level = MemberLevel.NORMAL;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum MemberLevel {
        NORMAL(1.0),
        SILVER(0.95),
        GOLD(0.9),
        PLATINUM(0.85),
        DIAMOND(0.8);

        private final double discount;

        MemberLevel(double discount) {
            this.discount = discount;
        }

        public double getDiscount() {
            return discount;
        }
    }
}
