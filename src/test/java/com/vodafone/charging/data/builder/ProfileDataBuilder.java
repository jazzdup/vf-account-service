package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.model.Profile;

import java.util.Arrays;

public class ProfileDataBuilder {


    public static Profile aProfile() {
        return Profile.builder()
                .userGroups(Arrays.asList("ug1", "ug2"))
                .lastUpdatedTransactions(MongoDataBuilder.aFixedDate())
                .lastUpdatedUserGroups(MongoDataBuilder.aFixedDate())
                .transactions(Arrays.asList(MongoDataBuilder.aTransaction()))
                .spendLimits(SpendLimitInfoDataBuilder.aSpendLimitList())
                .build();
    }

    public static Profile aProfileWithoutSpendLimits() {
        return Profile.builder()
                .userGroups(Arrays.asList("ug1", "ug2"))
                .lastUpdatedTransactions(MongoDataBuilder.aFixedDate())
                .lastUpdatedUserGroups(MongoDataBuilder.aFixedDate())
                .transactions(Arrays.asList(MongoDataBuilder.aTransaction()))
                .build();
    }

}
