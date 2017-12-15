package com.vodafone.charging.data.builder;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;

/**
 * Represents Account information after interaction with an external system e.g. IF Handlers
 */
public class EnrichedAccountInfoDataBuilder {

    public static EnrichedAccountInfo aEnrichedAccountInfo() {

        return new EnrichedAccountInfo.Builder("OK")
                .validationStatus("OK")
                .usergroups(Lists.newArrayList("user-group1", "user-group2"))
                .ban("123456_ban")
                .billingCycleDay(1)
                .serviceProviderId("serviceProviderId")
                .childServiceProviderId("childServiceProviderId")
                .serviceProviderType("serviceProviderType")
                .isPrepay(false)
                .errorId("test-error-id")
                .errorDescription("test-error-description")
                .build();
    }
}
