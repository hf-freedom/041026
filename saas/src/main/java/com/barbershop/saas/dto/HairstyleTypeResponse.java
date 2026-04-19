package com.barbershop.saas.dto;

import com.barbershop.saas.entity.HairstyleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HairstyleTypeResponse {
    private Long id;
    private Long merchantId;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;

    public static HairstyleTypeResponse from(HairstyleType type) {
        HairstyleTypeResponse response = new HairstyleTypeResponse();
        response.setId(type.getId());
        response.setMerchantId(type.getMerchantId());
        response.setName(type.getName());
        response.setDescription(type.getDescription());
        response.setImageUrl(type.getImageUrl());
        response.setActive(type.getActive());
        response.setCreatedAt(type.getCreatedAt());
        return response;
    }
}
