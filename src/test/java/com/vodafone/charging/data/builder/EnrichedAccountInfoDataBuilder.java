package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;

import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.exception.ErrorIds.VAS_INTERNAL_SERVER_ERROR;
import static java.lang.String.valueOf;

/**
 * Represents Account information after interaction with an external system e.g. IF Handlers
 */
public class EnrichedAccountInfoDataBuilder {

    public static EnrichedAccountInfo aEnrichedAccountInfo() {

        Random random = new Random();

        return new EnrichedAccountInfo.Builder("OK")
                .usergroups(newArrayList(valueOf(random.nextInt()), valueOf(random.nextInt())))
                .ban(random.nextInt() + "_ban")
                .billingCycleDay(random.nextInt())
                .serviceProviderId("serviceProviderId")
                .childServiceProviderId("childServiceProviderId")
                .serviceProviderType("serviceProviderType")
                .customerType("PRE")
                .errorId("test-error-id")
                .errorDescription("test-error-description")
                .build();
    }

    /**
     * returns the same subset of fields as current ERIF test system
     * @param chargingId
     * @return
     */
    public static EnrichedAccountInfo aEnrichedAccountInfoForTestERIF(ChargingId chargingId) {
        return new EnrichedAccountInfo.Builder("ACCEPTED")
                .ban("BAN_" + chargingId.getValue())
                .errorId("OK")
                .build();
    }

    public static EnrichedAccountInfo aEnrichedAccountInfoWhen500Response() {
        return new EnrichedAccountInfo.Builder("ERROR")
                .errorId(VAS_INTERNAL_SERVER_ERROR.errorId())
                .errorDescription(VAS_INTERNAL_SERVER_ERROR.errorDescription()).build();

    }
}
