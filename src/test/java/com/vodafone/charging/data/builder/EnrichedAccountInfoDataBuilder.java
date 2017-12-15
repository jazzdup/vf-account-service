package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;

import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.valueOf;

/**
 * Represents Account information after interaction with an external system e.g. IF Handlers
 */
public class EnrichedAccountInfoDataBuilder {

    public static EnrichedAccountInfo aEnrichedAccountInfo() {

        Random random = new Random();

        return new EnrichedAccountInfo.Builder("OK")
                .validationStatus("OK")
                .usergroups(newArrayList(valueOf(random.nextInt()), valueOf(random.nextInt())))
                .ban(random.nextInt() + "_ban")
                .billingCycleDay(random.nextInt())
                .serviceProviderId("serviceProviderId")
                .childServiceProviderId("childServiceProviderId")
                .serviceProviderType("serviceProviderType")
                .isPrepay(false)
                .errorId("test-error-id")
                .errorDescription("test-error-description")
                .build();
    }
}
