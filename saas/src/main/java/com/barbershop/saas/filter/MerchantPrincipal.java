package com.barbershop.saas.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantPrincipal {
    private Long merchantId;
    private String username;
}
