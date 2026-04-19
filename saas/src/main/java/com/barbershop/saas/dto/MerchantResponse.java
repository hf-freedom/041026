package com.barbershop.saas.dto;

import com.barbershop.saas.entity.Merchant;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MerchantResponse {
    private Long id;
    private String username;
    private String shopName;
    private String address;
    private String phone;
    private String contactPerson;
    private String businessLicense;
    private Merchant.MerchantStatus status;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    public static MerchantResponse from(Merchant merchant) {
        MerchantResponse response = new MerchantResponse();
        response.setId(merchant.getId());
        response.setUsername(merchant.getUsername());
        response.setShopName(merchant.getShopName());
        response.setAddress(merchant.getAddress());
        response.setPhone(merchant.getPhone());
        response.setContactPerson(merchant.getContactPerson());
        response.setBusinessLicense(merchant.getBusinessLicense());
        response.setStatus(merchant.getStatus());
        response.setBalance(merchant.getBalance());
        response.setCreatedAt(merchant.getCreatedAt());
        response.setApprovedAt(merchant.getApprovedAt());
        return response;
    }
}
