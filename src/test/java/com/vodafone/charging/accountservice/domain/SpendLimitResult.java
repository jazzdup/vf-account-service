package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Builder
@Component
@Getter
public class SpendLimitResult {

    private boolean spendLimitBreached;
    private SpendLimitType failureCauseType;
    private String failureReason;

}
