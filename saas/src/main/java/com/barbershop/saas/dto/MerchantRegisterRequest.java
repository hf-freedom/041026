package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MerchantRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度需在3-50之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需在6-100之间")
    private String password;

    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    private String address;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String contactPerson;

    private String businessLicense;
}
