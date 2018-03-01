package com.vodafone.charging.accountservice.domain.model;

import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@Builder
@Getter
@ToString
public class SpendLimit {

    private SpendLimitType spendLimitType;
    private Double limit;
    private boolean active;

    public static List<SpendLimit> fromSpendLimitsInfo(List<SpendLimitInfo> spendLimitsInfo) {
        return newArrayList(
                spendLimitsInfo.stream().filter(Objects::nonNull)
                        .map(limitInfo -> SpendLimit.builder()
                                .limit(limitInfo.getLimit())
                                .spendLimitType(limitInfo.getSpendLimitType())
                                .active(limitInfo.isActive()).build())
                        .collect(Collectors.toList()));
    }
}
