package com.vodafone.charging.data.object;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.enums.PackageType;

import java.util.Locale;

public class NullableContextData extends ContextData {

    private String contextName;
    private Locale locale;
    private ChargingId chargingId;
    private String serviceId;
    private PackageType packageType;
    private String vendorId;
    private String clientId;
    private boolean kycCheck;


    public NullableContextData(String contextName,
                               Locale locale,
                               ChargingId chargingId,
                               String serviceId,
                               PackageType packageType,
                               String vendorId,
                               String clientId,
                               boolean kycCheck) {

        this.contextName = contextName;
        this.locale = locale;
        this.chargingId = chargingId;
        this.serviceId = serviceId;
        this.packageType = packageType;
        this.vendorId = vendorId;
        this.clientId = clientId;
        this.kycCheck = kycCheck;
    }

    public String getContextName() {
        return contextName;
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

    public PackageType getPackageType() {
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
