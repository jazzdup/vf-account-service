package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.Profile;

import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfile;

public class AccountDataBuilder {

    public static Account anAccount() {
        return Account.builder()
                .id(String.valueOf(new Random().nextInt()))
                .lastValidate(MongoDataBuilder.aFixedDate())
                .chargingId(aChargingId())
                .customerType("PRE")
                .billingCycleDay(new Random().nextInt(27) + 1)
                .profiles(newArrayList(aProfile()))
                .build();
    }

    public static Account anAccount(Profile profile) {
        return Account.builder()
                .id(String.valueOf(new Random().nextInt()))
                .lastValidate(MongoDataBuilder.aFixedDate())
                .chargingId(aChargingId())
                .customerType("PRE")
                .billingCycleDay(new Random().nextInt(27) + 1)
                .profiles(newArrayList(profile))
                .build();
    }

    public static Account anAccountWithNullProfile() {
        return Account.builder()
                .id(String.valueOf(new Random().nextInt()))
                .lastValidate(MongoDataBuilder.aFixedDate())
                .chargingId(aChargingId())
                .customerType("PRE")
                .billingCycleDay(new Random().nextInt(27) + 1)
                .build();
    }
    public static Account anAccountWithEmptyProfile() {
        return Account.builder()
                .id(String.valueOf(new Random().nextInt()))
                .lastValidate(MongoDataBuilder.aFixedDate())
                .chargingId(aChargingId())
                .customerType("PRE")
                .billingCycleDay(new Random().nextInt(27) + 1)
                .profiles(newArrayList())
                .build();
    }
}
