package com.barbershop.saas.dto;

import com.barbershop.saas.entity.Barber;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BarberResponse {
    private Long id;
    private Long merchantId;
    private String name;
    private String phone;
    private Barber.BarberLevel level;
    private BigDecimal commissionRate;
    private Boolean active;
    private LocalDateTime createdAt;

    public static BarberResponse from(Barber barber) {
        BarberResponse response = new BarberResponse();
        response.setId(barber.getId());
        response.setMerchantId(barber.getMerchantId());
        response.setName(barber.getName());
        response.setPhone(barber.getPhone());
        response.setLevel(barber.getLevel());
        response.setCommissionRate(barber.getCommissionRate());
        response.setActive(barber.getActive());
        response.setCreatedAt(barber.getCreatedAt());
        return response;
    }
}
