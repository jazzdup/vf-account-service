package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.EnrichedAccountData;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Represents a Validation
 */
public class ValidationDataBuilder {

    public static EnrichedAccountData aValidation() {
        return new EnrichedAccountData.Builder()
                .usergroups(newArrayList("test-usergroup"))
                .result(true)
                .build();
    }
}
