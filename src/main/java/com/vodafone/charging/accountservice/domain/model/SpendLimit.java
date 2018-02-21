package com.vodafone.charging.accountservice.domain.model;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Getter
@ToString
public class SpendLimit {

    private SpendLimitType spendLimitType;
    private Integer limit;
    private boolean active;

    public static List<SpendLimit> fromSpendLimitInfo(List<SpendLimitInfo> spendLimitInfo) {
        return Lists.newArrayList(
                spendLimitInfo.stream().filter(Objects::nonNull)
                        .map(limitInfo -> SpendLimit.builder()
                                .limit(limitInfo.getLimit())
                                .spendLimitType(limitInfo.getSpendLimitType())
                                .active(limitInfo.isActive()).build())
                        .collect(Collectors.toList()));
    }

}
