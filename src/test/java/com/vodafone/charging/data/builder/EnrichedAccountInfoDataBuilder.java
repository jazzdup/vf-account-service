package com.vodafone.charging.data.builder;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;

/**
 * Represents a Validation
 */
public class EnrichedAccountInfoDataBuilder {

    public static EnrichedAccountInfo aEnrichedAccountInfo() {

        return new EnrichedAccountInfo.Builder()
                .validationStatus("OK")
                .usergroups(Lists.newArrayList("user-group1", "user-group2"))
                .ban("123456_ban")
                .billingCycleDay(1)
                .serviceProviderId("serviceProviderId")
                .childServiceProviderId("childServiceProviderId")
                .serviceProviderType("serviceProviderType")
                .errorId("test-error-id")
                .errorDescription("test-error-description")
                .build();
    }
}
