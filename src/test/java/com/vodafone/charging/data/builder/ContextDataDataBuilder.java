package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ContextData;

import java.util.Locale;
import java.util.Random;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;

/**
 * Convenient way to build an AccountSummary for tests
 */
public class ContextDataDataBuilder {

    public static ContextData aContextData() {
        return new ContextData.Builder("contextName", Locale.UK, aChargingId())
                .clientId("clientId")
                .serviceId("serviceId")
                .vendorId("vendor")
                .packageType("packageType")
                .kycCheck(false)
                .build();
    }

    public static ContextData aContextDataWithNullContextName() {
        return new ContextData.Builder("", Locale.UK, aChargingId())
                .clientId("clientId")
                .serviceId("serviceId")
                .vendorId("vendor")
                .packageType("packageType")
                .kycCheck(false)
                .build();
    }

    private static String generateAccountId() {
        return String.valueOf(new Random().nextInt());
    }

}
