package com.vodafone.charging.accountservice.domain.enums;

import java.util.ArrayList;
import java.util.List;

public abstract class Headers {
    private static final String TARGET = "x-vf-target";

    //This equates to the ENVIRONMENT_TYPE parameter in jbctl script e.g. LIVE / STAGING
    private final String ENVIRONMENT_PROPERTY_SERVER_PROP="central.configuration.env.type";
    private final List<String> headerNamesAdded = new ArrayList<>();
    private static final String PROP_REQUEST_MSISDN_HEADER_NAME ="request.msisdn.header.name";
    public static String REQUEST_MSISDN_HEADER_NAME;
    private static final String PROP_REQUEST_CLIENT_ID_HEADER_NAME = "request.clientid.header.name";
    public static String REQUEST_CLIENT_ID_HEADER_NAME;
    private static final String PROP_REQUEST_PARTNER_ID_HEADER_NAME = "request.partnerid.header.name";
    public static String REQUEST_PARTNER_ID_HEADER_NAME ;
    private static final String PROP_REQUEST_PACKAGE_ID_HEADER_NAME = "request.packageid.header.name";
    public static String REQUEST_PACKAGE_ID_HEADER_NAME ;
    public static String REQUEST_CHARGING_ID_HEADER_NAME="x-vf-charging-identifier";

}
