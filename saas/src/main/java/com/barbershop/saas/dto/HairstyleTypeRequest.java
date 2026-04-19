package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class HairstyleTypeRequest {
    @NotBlank(message = "发型名称不能为空")
    private String name;

    private String description;

    private String imageUrl;
}
