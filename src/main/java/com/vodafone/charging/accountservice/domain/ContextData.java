package com.vodafone.charging.accountservice.domain;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Represents operation context
 */
@Component
public class ContextData {

//    "chargingId", "ban", "userGroups", "serviceId", "partnerId", "packageType", "vendorId", "clientId", "kycCheck"

    private String id;
    private Locale locale;
    private ChargingId chargingId;
    private String serviceId; //should be an optional
    private String packageType;
    private String vendorId;
    private String clientId;
    private boolean kycCheck;
    //TODO Do we require the below in the request?
//    private String ban;
//    private List<String> userGroups;

    private ContextData() {
    }

    public ContextData(final String id, final Locale locale, final ChargingId chargingId,
                       final String serviceId, final String packageType, final String vendorId, final String clientId,
                       final boolean kycCheck) {
        this.id = id;
        this.locale = locale;
        this.chargingId = chargingId;
        this.serviceId = serviceId;
        this.packageType = packageType;
        this.vendorId = vendorId;
        this.clientId = clientId;
        this.kycCheck = kycCheck;

    }

    public String getId() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public ChargingId getChargingId() {
        return chargingId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isKycCheck() {
        return kycCheck;
    }
}
