package com.vodafone.charging.validator;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.enums.ValidateHttpHeaderName;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.*;

/**
 * Testing aid methods to avoid repetition around checking HttpHeader values
 * Methods are static for convenience.
 */
public class HttpHeaderValidator {


    public static void validateHttpHeadersJson(final HttpHeaders headers, final ContextData contextData) {

        assertThat(headers).isNotNull();
        assertThat(headers.getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(headers.getAccept()).containsExactly(APPLICATION_JSON, APPLICATION_JSON_UTF8);
        validateHttpHeaders(headers, contextData);

    }
    public static void validateHttpHeadersXml(final HttpHeaders headers, final ContextData contextData) {
        assertThat(headers).isNotNull();
        assertThat(headers.getContentType()).isEqualTo(TEXT_XML);
        assertThat(headers.getAccept()).containsExactly(TEXT_XML);
        validateHttpHeaders(headers, contextData);

    }

    private static void validateHttpHeaders(HttpHeaders headers, ContextData contextData) {
        assertThat(headers.get(ValidateHttpHeaderName.COUNTRY_HEADER_NAME.getName()))
                .containsExactly(contextData.getLocale().getCountry());
        assertThat(headers.get(ValidateHttpHeaderName.TARGET_HEADER_NAME.getName()))
                .containsExactly(contextData.getTarget().getValue());
        assertThat(headers.get(ValidateHttpHeaderName.REQUEST_CHARGING_ID_HEADER_NAME.getName()))
                .containsExactly(contextData.getChargingId().toIfString());

        assertThat(headers.get(ValidateHttpHeaderName.REQUEST_MSISDN_HEADER_NAME.getName()))
                .containsExactly(contextData.getChargingId().getValue());
        assertThat(headers.get(ValidateHttpHeaderName.REQUEST_CLIENT_ID_HEADER_NAME.getName()))
                .containsExactly(contextData.getClientId());
        assertThat(headers.get(ValidateHttpHeaderName.REQUEST_PARTNER_ID_HEADER_NAME.getName()))
                .containsExactly(contextData.getPartnerId());
        //TODO packageId is this required for Validate?
//        httpHeaders.set(ValidateHttpHeaderName.REQUEST_PACKAGE_ID_HEADER_NAME.getName(), contextData.getPackageId());
        assertThat(headers.get(ValidateHttpHeaderName.REQUEST_CLASS_HEADER_NAME.getName()))
                .containsExactly("VALIDATE");
    }

}
