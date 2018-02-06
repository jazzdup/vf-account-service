package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.ValidateHttpHeaderName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(MockitoJUnitRunner.class)
public class ValidateHttpHeadersTest {

    @Test
    public void shouldCreateAllValidateHeadersCorrectly() {
        ContextData contextData = aContextData();
        ValidateHttpHeaders validateHttpHeaders = new ValidateHttpHeaders(contextData);

        assertThat(validateHttpHeaders).isNotNull();
        assertThat(validateHttpHeaders.getHttpHeaders()).isNotNull();
        assertThat(validateHttpHeaders.getHttpHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(validateHttpHeaders.getHttpHeaders().getAccept()).containsExactly(APPLICATION_JSON, APPLICATION_JSON_UTF8);
        assertThat(validateHttpHeaders.getHttpHeaders().get(ValidateHttpHeaderName.COUNTRY_HEADER_NAME.getName()))
                .containsExactly(contextData.getLocale().getCountry());
        assertThat(validateHttpHeaders.getHttpHeaders().get(ValidateHttpHeaderName.TARGET_HEADER_NAME.getName()))
                .containsExactly(contextData.getTarget().getValue());
        assertThat(validateHttpHeaders.getHttpHeaders().get(ValidateHttpHeaderName.REQUEST_CHARGING_ID_HEADER_NAME.getName()))
                .containsExactly(contextData.getChargingId().toIfString());

        assertThat(validateHttpHeaders.getHttpHeaders().get(ValidateHttpHeaderName.REQUEST_MSISDN_HEADER_NAME.getName()))
                .containsExactly(contextData.getChargingId().getValue());
        assertThat(validateHttpHeaders.getHttpHeaders().get(ValidateHttpHeaderName.REQUEST_CLIENT_ID_HEADER_NAME.getName()))
                .containsExactly(contextData.getClientId());
        assertThat(validateHttpHeaders.getHttpHeaders().get(ValidateHttpHeaderName.REQUEST_PARTNER_ID_HEADER_NAME.getName()))
                .containsExactly(contextData.getPartnerId());
        //TODO packageId is this required for Validate?
//        httpHeaders.set(ValidateHttpHeaderName.REQUEST_PACKAGE_ID_HEADER_NAME.getName(), contextData.getPackageId());
        assertThat(validateHttpHeaders.getHttpHeaders().get(ValidateHttpHeaderName.REQUEST_CLASS_HEADER_NAME.getName()))
                .containsExactly("VALIDATE");

                //TODO finish all possibilities here

    }
}
