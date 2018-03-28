package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

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

    public static List<SpendLimitInfo> from(List<SpendLimit> spendLimits) {
        return newArrayList(
                spendLimits.stream().filter(Objects::nonNull)
                        .map(l -> SpendLimitInfo.builder()
                                .limit(l.getLimit())
                                .spendLimitType(l.getSpendLimitType())
                                .build())
                        .collect(Collectors.toList()));

    }
}
