package com.barbershop.saas.dto;

import com.barbershop.saas.entity.ConsumptionRecord;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ConsumeRequest {
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotNull(message = "理发师ID不能为空")
    private Long barberId;

    private Long hairstyleTypeId;

    @NotNull(message = "原价不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal originalAmount;

    @NotNull(message = "支付方式不能为空")
    private ConsumptionRecord.PaymentMethod paymentMethod;

    private List<Long> hairstyleOptionIds;

    private String remark;
}
