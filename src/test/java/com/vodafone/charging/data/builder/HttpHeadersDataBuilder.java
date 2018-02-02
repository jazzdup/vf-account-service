package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import org.springframework.http.HttpHeaders;

import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class HttpHeadersDataBuilder {

    private static final String TARGET_HEADER_NAME = "x-vf-target";
    private static final String COUNTRY_HEADER_NAME = "country";
    private static final String ENVIRONMENT_PROPERTY_SERVER_NAME = "environment";
    private static final String REQUEST_CHARGING_ID_HEADER_NAME="x-vf-charging-identifier";
    private static final String REQUEST_MSISDN_HEADER_NAME = "x-msisdn";
    private static final String REQUEST_CLIENT_ID_HEADER_NAME = "x-clientid";
    private static final String REQUEST_PARTNER_ID_HEADER_NAME = "x-partnerid" ;
    private static final String REQUEST_PACKAGE_ID_HEADER_NAME = "x-packageid";
    private static final String REQUEST_CLASS_HEADER_NAME = "x-vf-request-class";

    public static HttpHeaders aHttpHeaders(String clientId, Locale locale, ChargingId chargingId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(newArrayList(APPLICATION_JSON));
        headers.add(TARGET_HEADER_NAME, "LOCAL");
        headers.add(COUNTRY_HEADER_NAME, locale.getCountry());
        headers.add(ENVIRONMENT_PROPERTY_SERVER_NAME, "LIVE");
        headers.add(REQUEST_CHARGING_ID_HEADER_NAME, chargingId.getValue());
        if(chargingId.getType().equalsIgnoreCase(ChargingId.Type.MSISDN.type())) {
            headers.add(REQUEST_MSISDN_HEADER_NAME, chargingId.getValue());
        }
        headers.add(REQUEST_CHARGING_ID_HEADER_NAME, chargingId.getValue());
        headers.add(REQUEST_CLIENT_ID_HEADER_NAME, clientId);
        headers.add(REQUEST_PARTNER_ID_HEADER_NAME, "test-partner-id");
        headers.add(REQUEST_PACKAGE_ID_HEADER_NAME, "test-package-id");
        headers.add(REQUEST_CLASS_HEADER_NAME, "VALIDATE");

        return headers;

    }
}
