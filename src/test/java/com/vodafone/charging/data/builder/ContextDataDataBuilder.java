package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.ChargingId;

import java.util.Locale;
import java.util.Random;

import static com.vodafone.charging.accountservice.domain.ChargingId.Type;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.*;

/**
 * Convenient way to build an AccountSummary for tests
 */
public class ContextDataDataBuilder {

    public static ContextData aAccount() {
        return new ContextData(generateAccountId(),
                Locale.UK,
                new ChargingId.Builder()
                        .type(Type.MSISDN)
                        .value(generateChargingId()).build(),
                "serviceId",
                "packageType",
                "vendorId",
                "clientId",
                false);
    }

    public static ContextData aAccountWithNullAccountId() {
        return new ContextData(null,
                Locale.UK,
                new ChargingId.Builder()
                        .type(Type.MSISDN)
                        .value(generateChargingId()).build(),
                "serviceId",
                "packageType",
                "vendorId",
                "clientId",
                false);
    }

    private static String generateAccountId() {
        return String.valueOf(new Random().nextInt());
    }

}
