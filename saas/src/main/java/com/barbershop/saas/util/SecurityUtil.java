package com.barbershop.saas.util;

import com.barbershop.saas.filter.MerchantPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    public Long getCurrentMerchantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MerchantPrincipal) {
            return ((MerchantPrincipal) authentication.getPrincipal()).getMerchantId();
        }
        return null;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MerchantPrincipal) {
            return ((MerchantPrincipal) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
