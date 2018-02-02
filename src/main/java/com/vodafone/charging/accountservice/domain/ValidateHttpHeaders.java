package com.vodafone.charging.accountservice.domain;

import org.springframework.http.HttpHeaders;

public class ValidateHttpHeaders {

    public static final String TARGET_HEADER_NAME = "x-vf-target";
    public static final String COUNTRY_HEADER_NAME = "country";
    public static final String ENVIRONMENT_PROPERTY_SERVER_NAME = "environment";
    public static final String REQUEST_CHARGING_ID_HEADER_NAME="x-vf-charging-identifier";
    public static final String REQUEST_MSISDN_HEADER_NAME = "x-msisdn";
    public static final String REQUEST_CLIENT_ID_HEADER_NAME = "x-clientid";
    public static final String REQUEST_PARTNER_ID_HEADER_NAME = "x-partnerid" ;
    public static final String REQUEST_PACKAGE_ID_HEADER_NAME = "x-packageid";
    public static final String REQUEST_CLASS_HEADER_NAME = "x-vf-request-class";

    private HttpHeaders headers = new HttpHeaders();

    public ValidateHttpHeaders(ERIFRequest request) {
//        headers.add(TARGET_HEADER_NAME, request);

    }

}
