package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;

import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

public class SpendLimitInfoDataBuilder {

    public static List<SpendLimitInfo> aSpendLimitInfoList() {
        return newArrayList(SpendLimitInfo.builder()
                        .limit(new Random().nextInt(1))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimitInfo.builder()
                        .limit(new Random().nextInt(12))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimitInfo.builder()
                        .limit(new Random().nextInt(13))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

    public static List<SpendLimit> aSpendLimitList() {
        return newArrayList(SpendLimit.builder()
                        .limit(new Random().nextInt(1))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimit.builder()
                        .limit(new Random().nextInt(1))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimit.builder()
                        .limit(new Random().nextInt(1))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build());
    }

}
