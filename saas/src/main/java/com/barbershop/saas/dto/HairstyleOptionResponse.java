package com.barbershop.saas.dto;

import com.barbershop.saas.entity.HairstyleOption;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HairstyleOptionResponse {
    private Long id;
    private Long merchantId;
    private String name;
    private String category;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;

    public static HairstyleOptionResponse from(HairstyleOption option) {
        HairstyleOptionResponse response = new HairstyleOptionResponse();
        response.setId(option.getId());
        response.setMerchantId(option.getMerchantId());
        response.setName(option.getName());
        response.setCategory(option.getCategory());
        response.setDescription(option.getDescription());
        response.setActive(option.getActive());
        response.setCreatedAt(option.getCreatedAt());
        return response;
    }
}
