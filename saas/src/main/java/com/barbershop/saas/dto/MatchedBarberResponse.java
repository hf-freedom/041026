package com.barbershop.saas.dto;

import com.barbershop.saas.entity.Barber;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MatchedBarberResponse {
    private Long id;
    private String name;
    private String phone;
    private Barber.BarberLevel level;
    private BigDecimal commissionRate;
    private Integer matchScore;

    public static MatchedBarberResponse from(Barber barber, int score) {
        MatchedBarberResponse response = new MatchedBarberResponse();
        response.setId(barber.getId());
        response.setName(barber.getName());
        response.setPhone(barber.getPhone());
        response.setLevel(barber.getLevel());
        response.setCommissionRate(barber.getCommissionRate());
        response.setMatchScore(score);
        return response;
    }
}
