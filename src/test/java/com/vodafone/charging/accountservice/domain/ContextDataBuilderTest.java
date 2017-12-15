package com.vodafone.charging.accountservice.domain;

import org.junit.Test;

import java.util.Locale;

import static com.vodafone.charging.accountservice.domain.enums.PackageType.CALENDAR_PACKAGE_TYPE;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContextDataBuilderTest {

    @Test
    public void shouldNotAllowNullContextName() {

        assertThatThrownBy(() -> createContextData(null, Locale.UK, aChargingId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("contextName is null");

    }

    @Test
    public void shouldNotAllowNullLocale() {
        assertThatThrownBy(() -> createContextData("test-context-name", null, aChargingId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("locale is null");
    }

    @Test
    public void shouldNotAllowNullChargingId() {
        assertThatThrownBy(() -> createContextData("test-context-name", Locale.UK, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId is null");
    }

    private ContextData createContextData(String contextName, Locale locale, ChargingId chargingId) {
        return new ContextData.Builder(contextName, locale, chargingId)
                .clientId("clientId")
                .serviceId("serviceId")
                .vendorId("vendor")
                .packageType(CALENDAR_PACKAGE_TYPE)
                .kycCheck(false)
                .build();
    }
}
