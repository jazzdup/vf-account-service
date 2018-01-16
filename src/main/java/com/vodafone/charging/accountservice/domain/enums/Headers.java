package com.vodafone.charging.accountservice.domain.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 15/01/18.
 */
public abstract class Headers {
    private static final String TARGET = "x-vf-target";

    //This equates to the ENVIRONMENT_TYPE parameter in jbctl startup script
    private final String ENVIRONMENT_PROPERTY_SERVER_PROP="central.configuration.env.type";
    private final List<String> headerNamesAdded = new ArrayList<>();

    private static final String PROP_REQUEST_MSISDN_HEADER_NAME ="request.msisdn.header.name";
    /** Constant <code>REQUEST_MSISDN_HEADER_NAME=""</code> */
    public static String REQUEST_MSISDN_HEADER_NAME;

    private static final String PROP_REQUEST_CLIENT_ID_HEADER_NAME = "request.clientid.header.name";
    /** Constant <code>REQUEST_CLIENT_ID_HEADER_NAME=""</code> */
    public static String REQUEST_CLIENT_ID_HEADER_NAME;

    private static final String PROP_REQUEST_PARTNER_ID_HEADER_NAME = "request.partnerid.header.name";
    /** Constant <code>REQUEST_PARTNER_ID_HEADER_NAME=""</code> */
    public static String REQUEST_PARTNER_ID_HEADER_NAME ;

    private static final String PROP_REQUEST_PACKAGE_ID_HEADER_NAME = "request.packageid.header.name";
    /** Constant <code>REQUEST_PACKAGE_ID_HEADER_NAME=""</code> */
    public static String REQUEST_PACKAGE_ID_HEADER_NAME ;

    /** Constant <code>REQUEST_CHARGING_ID_HEADER_NAME="x-vf-charging-identifier"</code> */
    public static String REQUEST_CHARGING_ID_HEADER_NAME="x-vf-charging-identifier";

}
