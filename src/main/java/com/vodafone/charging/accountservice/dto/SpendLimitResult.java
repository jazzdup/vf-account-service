package com.vodafone.charging.accountservice.dto;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Builder
@Component
@Getter
public class SpendLimitResult {

    private boolean success;
    @Nullable
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
