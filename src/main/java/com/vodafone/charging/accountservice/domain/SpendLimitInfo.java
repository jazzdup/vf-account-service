package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @ToString
public class SpendLimitInfo {

    private SpendLimitType spendLimitType;
    private Integer limit;
    private boolean active;

}
