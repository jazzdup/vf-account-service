package com.vodafone.charging.data.builder;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

public class SpendLimitDataBuilder {

    public static List<SpendLimitInfo> aSpendLimitInfoList() {
        return newArrayList(SpendLimitInfo.builder()
                        .limit(BigDecimal.valueOf(new Random().nextDouble()).setScale(2, RoundingMode.HALF_UP)
                                .doubleValue())
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimitInfo.builder()
                        .limit(BigDecimal.valueOf(new Random().nextDouble()).setScale(2, RoundingMode.HALF_UP)
                                .doubleValue())
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimitInfo.builder()
                        .limit(BigDecimal.valueOf(new Random().nextDouble()).setScale(2, RoundingMode.HALF_UP)
                                .doubleValue())
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static List<SpendLimitInfo> aSpendLimitInfoList(double txLimit, double dayLimit, double monthLimit) {
        return newArrayList(SpendLimitInfo.builder()
                        .limit(txLimit)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimitInfo.builder()
                        .limit(dayLimit)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimitInfo.builder()
                        .limit(monthLimit)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static SpendLimit aSpendLimit(double limit, SpendLimitType type) {
        return SpendLimit.builder()
                .limit(limit)
                .spendLimitType(type).build();
    }
    public static SpendLimitInfo aSpendLimitInfo(double limit, SpendLimitType type) {
        return SpendLimitInfo.builder()
                .limit(limit)
                .active(true)
                .spendLimitType(type).build();
    }

    public static List<SpendLimit> aSpendLimitList(double txLimit, double dayLimit, double monthLimit) {
        return newArrayList(SpendLimit.builder()
                        .limit(txLimit)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimit.builder()
                        .limit(dayLimit)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimit.builder()
                        .limit(monthLimit)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static List<SpendLimit> aStandardSpendLimitList() {
        return newArrayList(SpendLimit.builder()
                        .limit(2.15)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimit.builder()
                        .limit(10.21)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimit.builder()
                        .limit(50.69)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static List<SpendLimit> aStandardDefaultSpendLimitList() {
        return newArrayList(SpendLimit.builder()
                        .limit(4.15)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimit.builder()
                        .limit(11.21)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimit.builder()
                        .limit(52.69)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static Map.Entry<List<SpendLimit>, List<SpendLimit>> aSpendLimitAndDefaultPairList() {
        return Maps.immutableEntry(aStandardSpendLimitList(), aStandardDefaultSpendLimitList());
    }

}
