package com.vodafone.charging.accountservice.domain.model;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @ToString
public class SpendLimit {

    private SpendLimitType spendLimitType;
    private Integer limit;
    private boolean active;

}
