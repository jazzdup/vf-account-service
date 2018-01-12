package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;

import java.util.Calendar;
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
                .isPrepay(false)
                .errorId("test-error-id")
                .errorDescription("test-error-description")
                .build();
    }

    public static EnrichedAccountInfo aEnrichedAccountInfo(ChargingId chargingId) {
        Random random = new Random();
        return new EnrichedAccountInfo.Builder("ACCEPTED")
//                .usergroups(newArrayList(valueOf(random.nextInt()), valueOf(random.nextInt())))//@TODO
                .ban("BAN_" + chargingId.getValue())
                .billingCycleDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
//                .serviceProviderId("serviceProviderId")
//                .childServiceProviderId("childServiceProviderId")
//                .serviceProviderType("serviceProviderType")
//                .isPrepay(false)
                .errorId("OK")
//                .errorDescription("test-error-description")
                .build();
    }

    public static EnrichedAccountInfo aEnrichedAccountInfoWhen500Response() {
        return new EnrichedAccountInfo.Builder("fail")
                .errorId(VAS_INTERNAL_SERVER_ERROR.errorId())
                .errorDescription(VAS_INTERNAL_SERVER_ERROR.errorDescription()).build();

    }
}
