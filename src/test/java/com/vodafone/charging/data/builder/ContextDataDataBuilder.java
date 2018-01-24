package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;

import java.util.Locale;

import static com.vodafone.charging.accountservice.domain.enums.PackageType.CALENDAR_PACKAGE_TYPE;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;

/**
 * Convenient way to build an AccountSummary for tests
 */
public class ContextDataDataBuilder {

    public static ContextData aContextData() {
        return new ContextData.Builder("contextName", Locale.UK, aChargingId())
                .clientId("clientId")
                .serviceId("sAlt")
                .vendorId("vendorId")
                .packageType(CALENDAR_PACKAGE_TYPE)
                .kycCheck(false)
                .build();
    }
    public static ContextData aContextData(ChargingId chargingId) {
        return new ContextData.Builder("contextName", Locale.UK, chargingId)
                .clientId("clientId")
                .serviceId("sAlt")
                .vendorId("vendorId")
                .packageType(CALENDAR_PACKAGE_TYPE)
                .kycCheck(false)
                .build();
    }
    public static ContextData aContextData(String contextName, Locale locale, ChargingId chargingId) {
        return new ContextData.Builder(contextName, locale, chargingId)
                .clientId("test-clientId")
                .serviceId("test-serviceId")
                .vendorId("test-vendor")
                .packageType(CALENDAR_PACKAGE_TYPE)
                .kycCheck(false)
                .build();
    }

}
