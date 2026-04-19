package com.barbershop.saas.dto;

import com.barbershop.saas.entity.Member;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberResponse {
    private Long id;
    private Long merchantId;
    private String name;
    private String phone;
    private Member.MemberLevel level;
    private Double discount;
    private BigDecimal balance;
    private BigDecimal totalConsumption;
    private LocalDateTime createdAt;

    public static MemberResponse from(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setMerchantId(member.getMerchantId());
        response.setName(member.getName());
        response.setPhone(member.getPhone());
        response.setLevel(member.getLevel());
        response.setDiscount(member.getLevel().getDiscount());
        response.setBalance(member.getBalance());
        response.setTotalConsumption(member.getTotalConsumption());
        response.setCreatedAt(member.getCreatedAt());
        return response;
    }
}
