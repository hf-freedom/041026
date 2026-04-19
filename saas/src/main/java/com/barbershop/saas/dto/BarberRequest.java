package com.barbershop.saas.dto;

import com.barbershop.saas.entity.Barber;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class BarberRequest {
    @NotBlank(message = "理发师姓名不能为空")
    private String name;

    private String phone;

    private Barber.BarberLevel level;

    @DecimalMin(value = "0.0", message = "提成比例不能小于0")
    @DecimalMax(value = "1.0", message = "提成比例不能大于1")
    private BigDecimal commissionRate;
}
