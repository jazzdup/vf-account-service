package com.vodafone.charging.accountservice.dto;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpendLimitResult {

    private boolean success;
    private SpendLimitType failureCauseType;
    private String failureReason;
    private double totalTransactionsValue;
    private double appliedLimitValue;


    public static SpendLimitResult successResponse(double appliedLimitValue, double totalTransactionsValue) {
        return SpendLimitResult.builder()
                .success(true)
                .failureReason("")
                .appliedLimitValue(appliedLimitValue)
                .totalTransactionsValue(totalTransactionsValue)
                .build();
    }

}
