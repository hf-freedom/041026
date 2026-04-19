package com.barbershop.saas.dto;

import com.barbershop.saas.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long merchantId;
    private Long memberId;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String remark;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setMerchantId(transaction.getMerchantId());
        response.setMemberId(transaction.getMemberId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setRemark(transaction.getRemark());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}
