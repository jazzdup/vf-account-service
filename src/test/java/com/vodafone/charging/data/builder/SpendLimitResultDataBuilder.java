package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;

import java.util.Random;

public class SpendLimitResultDataBuilder {

    public static SpendLimitResult aSpendLimitResult(boolean result, String reasonMessage, SpendLimitType type) {
        return SpendLimitResult.builder().success(result)
                .totalTransactionsValue(new Random().nextDouble())
                .appliedLimitValue(new Random().nextDouble())
                .failureReason(reasonMessage)
                .failureCauseType(type)
                .build();
    }
}
