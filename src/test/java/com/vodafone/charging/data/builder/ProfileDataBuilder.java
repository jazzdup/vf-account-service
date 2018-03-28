package com.vodafone.charging.data.builder;

import com.google.common.collect.ImmutableList;
import com.vodafone.charging.accountservice.domain.model.Profile;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;

import java.util.Arrays;
import java.util.List;

public class ProfileDataBuilder {


    public static Profile aProfile() {
        return Profile.builder()
                .userGroups(Arrays.asList("ug1", "ug2"))
                .lastUpdatedTransactions(MongoDataBuilder.aFixedDate())
                .lastUpdatedUserGroups(MongoDataBuilder.aFixedDate())
                .transactions(ImmutableList.of(MongoDataBuilder.aTransaction()))
                .spendLimits(SpendLimitDataBuilder.aStandardSpendLimitList())
                .build();
    }

    public static Profile aProfile(List<SpendLimit> spendLimits) {
        return Profile.builder()
                .userGroups(Arrays.asList("ug1", "ug2"))
                .lastUpdatedTransactions(MongoDataBuilder.aFixedDate())
                .lastUpdatedUserGroups(MongoDataBuilder.aFixedDate())
                .transactions(ImmutableList.of(MongoDataBuilder.aTransaction()))
                .spendLimits(spendLimits)
                .build();
    }

    public static Profile aProfileWithoutSpendLimits() {
        return Profile.builder()
                .userGroups(Arrays.asList("ug1", "ug2"))
                .lastUpdatedTransactions(MongoDataBuilder.aFixedDate())
                .lastUpdatedUserGroups(MongoDataBuilder.aFixedDate())
                .transactions(ImmutableList.of(MongoDataBuilder.aTransaction()))
                .build();
    }

}
