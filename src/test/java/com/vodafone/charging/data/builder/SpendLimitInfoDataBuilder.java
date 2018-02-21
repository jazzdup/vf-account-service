package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;

import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

public class SpendLimitInfoDataBuilder {

    public static List<SpendLimitInfo> aSpendLimitList() {
        return newArrayList(SpendLimitInfo.builder()
                        .limit(new Random().nextInt(10))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_DAY).build(),
                SpendLimitInfo.builder()
                        .limit(new Random().nextInt(10))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_TX).build(),
                SpendLimitInfo.builder()
                        .limit(new Random().nextInt(10))
                        .active(true)
                        .spendLimitType(SpendLimitType.ACCOUNT_MONTH).build());
    }

}
