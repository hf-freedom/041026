package com.barbershop.saas.dto;

import lombok.Data;

import java.util.List;

@Data
public class MatchBarberRequest {
    private Long hairstyleTypeId;
    private List<Long> hairstyleOptionIds;
}
