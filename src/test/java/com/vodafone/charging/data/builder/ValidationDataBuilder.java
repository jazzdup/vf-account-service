package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.Validation;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Represents a Validation
 */
public class ValidationDataBuilder {

    public static Validation aValidation() {
        return new Validation.Builder()
                .usergroups(newArrayList("test-usergroup"))
                .result(true)
                .build();
    }
}
