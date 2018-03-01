package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Builder
@Getter
@ToString
@NoArgsConstructor
public class SpendLimitInfo {

    protected SpendLimitType spendLimitType;
    protected Double limit;
    protected boolean active;

    public SpendLimitInfo(SpendLimitType spendLimitType, Double limit, boolean active) {
        this.spendLimitType = spendLimitType;
        this.limit = limit;
        this.active = active;
    }
}
