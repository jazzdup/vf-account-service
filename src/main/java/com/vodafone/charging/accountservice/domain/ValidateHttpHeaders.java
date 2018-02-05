package com.vodafone.charging.accountservice.domain;

import org.springframework.http.HttpHeaders;

import static com.vodafone.application.util.Lists.newArrayList;
import static com.vodafone.charging.accountservice.domain.enums.ValidateHttpHeaderName.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * Collection to represent the headers required for ER IF
 */
public class ValidateHttpHeaders {

    private HttpHeaders httpHeaders;
    private static final String envTypeConfigPropName = "central.configuration.env.type";

    public ValidateHttpHeaders(ContextData contextData) {
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        httpHeaders.setAccept(newArrayList(APPLICATION_JSON, APPLICATION_JSON_UTF8));
        httpHeaders.set(COUNTRY_HEADER_NAME.getName(), contextData.getLocale().getCountry());
        httpHeaders.set(TARGET_HEADER_NAME.getName(), contextData.getLocale().getCountry());
        httpHeaders.set(REQUEST_CHARGING_ID_HEADER_NAME.getName(), contextData.getChargingId().toIfString());
        httpHeaders.set(REQUEST_MSISDN_HEADER_NAME.getName(), "");
        httpHeaders.set(REQUEST_CLIENT_ID_HEADER_NAME.getName(), contextData.getClientId());
        httpHeaders.set(REQUEST_PARTNER_ID_HEADER_NAME.getName(), contextData.getPartnerId());
        //TODO packageId is this required for Validate?
//        httpHeaders.set(ValidateHttpHeaderName.REQUEST_PACKAGE_ID_HEADER_NAME.getName(), contextData.getPackageId());
        httpHeaders.set(REQUEST_CLASS_HEADER_NAME.getName(), "VALIDATE");
//        httpHeaders.set(ENVIRONMENT_PROPERTY_SERVER_NAME.getName(), propertiesAccessor.getProperty(envTypeConfigPropName, ""));

    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }
}
