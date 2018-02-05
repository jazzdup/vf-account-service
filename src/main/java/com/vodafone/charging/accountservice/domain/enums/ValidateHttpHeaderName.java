package com.vodafone.charging.accountservice.domain.enums;

/**
 * Headers required to be passed to ER IF.
 * Most of these should arrive in the body of incoming post request.
 */
public enum ValidateHttpHeaderName {

    TARGET_HEADER_NAME("x-vf-target" ),
    COUNTRY_HEADER_NAME("country"),
    ENVIRONMENT_PROPERTY_SERVER_NAME("environment"),
    REQUEST_CHARGING_ID_HEADER_NAME("x-vf-charging-identifier"),
    REQUEST_MSISDN_HEADER_NAME("x-msisdn"),
    REQUEST_CLIENT_ID_HEADER_NAME("x-clientid"),
    REQUEST_PARTNER_ID_HEADER_NAME("x-partnerid"),
    REQUEST_PACKAGE_ID_HEADER_NAME("x-packageid"),
    //Currently this is just "VALIDATE".
    REQUEST_CLASS_HEADER_NAME("x-vf-request-class");

    ValidateHttpHeaderName(String headerName) {
        this.name = headerName;
    }

    private String name;

    public String getName() {
        return name;
    }

}
