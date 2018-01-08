package com.vodafone.charging.accountservice.domain;

import org.junit.Test;

import java.util.Locale;

import static com.vodafone.charging.accountservice.domain.enums.PackageType.EVENT;
import static com.vodafone.charging.accountservice.domain.enums.PackageType.EVENT_CALENDAR_PACKAGE_TYPE;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ContextDataTest {

    @Test
    public void shouldNotAllowNullContextName() {
        assertThatThrownBy(() -> aContextData(null, Locale.UK, aChargingId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("contextName is null");
    }

    @Test
    public void shouldNotAllowNullLocale() {
        assertThatThrownBy(() -> aContextData("test-context-name", null, aChargingId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("locale is null");
    }

    @Test
    public void shouldNotAllowNullChargingId() {
        assertThatThrownBy(() -> aContextData("test-context-name", Locale.UK, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId is null");
    }

    @Test
    public void checkGetters() {
        ChargingId chargingId = aChargingId();
        ContextData contextData = new ContextData.Builder("test-context-name", Locale.UK, chargingId)
                .clientId("clientId")
                .serviceId("serviceId")
                .vendorId("vendor")
                .packageType(EVENT_CALENDAR_PACKAGE_TYPE)
                .kycCheck(false)
                .build();

        assertThat(contextData.getContextName()).isEqualTo("test-context-name");
        assertThat(contextData.getLocale()).isEqualTo(Locale.UK);
        assertThat(contextData.getChargingId()).isEqualTo(chargingId);
        assertThat(contextData.getClientId()).isEqualTo("clientId");
        assertThat(contextData.getServiceId()).isEqualTo("serviceId");
        assertThat(contextData.getVendorId()).isEqualTo("vendor");
        assertThat(contextData.getPackageType()).isEqualTo(EVENT_CALENDAR_PACKAGE_TYPE);
        assertThat(contextData.isKycCheck()).isEqualTo(false);

    }

    @Test
    public void checkToString() {
        ChargingId chargingId = aChargingId();
        ContextData contextData = new ContextData.Builder("test-context-name", Locale.UK, chargingId)
                .clientId("test-clientId")
                .serviceId("test-serviceId")
                .vendorId("test-vendor")
                .packageType(EVENT)
                .kycCheck(false)
                .build();

        final String contextDataStr = contextData.toString();
        System.out.println("String value:\n\n " + contextDataStr);
        assertThat(contextDataStr).contains("test-context-name");
        assertThat(contextDataStr).contains(Locale.UK.toString());
        assertThat(contextDataStr).contains(chargingId.toString());
        assertThat(contextDataStr).contains("test-serviceId");
        assertThat(contextDataStr).contains("test-vendor");
        assertThat(contextDataStr).contains(EVENT.toString());
        assertThat(contextDataStr).contains("kycCheck=false");
    }

}