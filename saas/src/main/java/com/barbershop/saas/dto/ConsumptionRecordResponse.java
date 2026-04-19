package com.barbershop.saas.dto;

import com.barbershop.saas.entity.ConsumptionRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ConsumptionRecordResponse {
    private Long id;
    private Long merchantId;
    private Long memberId;
    private String memberName;
    private Long barberId;
    private String barberName;
    private Long hairstyleTypeId;
    private String hairstyleTypeName;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private BigDecimal barberCommission;
    private ConsumptionRecord.PaymentMethod paymentMethod;
    private String hairstyleOptions;
    private String remark;
    private LocalDateTime createdAt;

    public static ConsumptionRecordResponse from(ConsumptionRecord record) {
        ConsumptionRecordResponse response = new ConsumptionRecordResponse();
        response.setId(record.getId());
        response.setMerchantId(record.getMerchantId());
        response.setMemberId(record.getMemberId());
        response.setBarberId(record.getBarberId());
        response.setHairstyleTypeId(record.getHairstyleTypeId());
        response.setOriginalAmount(record.getOriginalAmount());
        response.setDiscountAmount(record.getDiscountAmount());
        response.setActualAmount(record.getActualAmount());
        response.setBarberCommission(record.getBarberCommission());
        response.setPaymentMethod(record.getPaymentMethod());
        response.setHairstyleOptions(record.getHairstyleOptions());
        response.setRemark(record.getRemark());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }
}
