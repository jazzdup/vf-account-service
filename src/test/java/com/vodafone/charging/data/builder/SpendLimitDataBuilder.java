package com.vodafone.charging.data.builder;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

public class SpendLimitDataBuilder {

    public static List<SpendLimitInfo> aSpendLimitInfoList() {
        return newArrayList(SpendLimitInfo.builder()
                        .limit(new Random().nextDouble())
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimitInfo.builder()
                        .limit(new Random().nextDouble())
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimitInfo.builder()
                        .limit(new Random().nextDouble())
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static SpendLimit aSpendLimit(double limit, SpendLimitType type) {
        return SpendLimit.builder()
                .limit(limit)
                .active(true)
                .spendLimitType(type).build();
    }
    public static SpendLimitInfo aSpendLimitInfo(double limit, SpendLimitType type) {
        return SpendLimitInfo.builder()
                .limit(limit)
                .active(true)
                .spendLimitType(type).build();
    }

    public static SpendLimit aDefaultSpendLimit(double limit, SpendLimitType type) {
        return SpendLimit.builder()
                .limit(limit)
                .active(true)
                .spendLimitType(type).build();

    }

    public static List<SpendLimit> aSpendLimitList() {
        return newArrayList(SpendLimit.builder()
                        .limit(2.15)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimit.builder()
                        .limit(10.21)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimit.builder()
                        .limit(50.69)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static List<SpendLimit> aDefaultSpendLimitList() {
        return newArrayList(SpendLimit.builder()
                        .limit(4.15)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimit.builder()
                        .limit(11.21)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimit.builder()
                        .limit(52.69)
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }




    public static Map.Entry<List<SpendLimit>, List<SpendLimit>> aSpendLimitAndDefaultPairList() {
        return Maps.immutableEntry(aSpendLimitList(), aDefaultSpendLimitList());
    }

}
