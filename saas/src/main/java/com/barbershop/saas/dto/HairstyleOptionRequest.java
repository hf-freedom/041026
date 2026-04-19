package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class HairstyleOptionRequest {
    @NotBlank(message = "选项名称不能为空")
    private String name;

    private String category;

    private String description;
}
