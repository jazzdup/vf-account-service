package com.vodafone.charging.accountservice.domain;

import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * Represents operation context
 * Could rename it RequestProperties
 */
@Component
public class ContextData {

    private String contextName;
    private Locale locale;
    private ChargingId chargingId;
    private String serviceId; //should be an optional
    private String packageType;
    private String vendorId;
    private String clientId;
    private boolean kycCheck;
    //TODO Do we require the below in the request?  Unlikely.
//    private String ban;
//    private List<String> userGroups;

    private ContextData() {
    }

    private ContextData(ContextData.Builder builder) {
        this.contextName = builder.contextName;
        this.locale = builder.locale;
        this.chargingId = builder.chargingId;
        this.serviceId = builder.serviceId;
        this.packageType = builder.packageType;
        this.vendorId = builder.vendorId;
        this.clientId = builder.clientId;
        this.kycCheck = builder.kycCheck;
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

    public static class Builder {

        private String contextName;
        private final Locale locale;
        private final ChargingId chargingId;
        private String serviceId; //should be optional
        private String packageType;
        private String vendorId; //optional
        private String clientId;
        private boolean kycCheck; //optional

        public Builder(@Nonnull String contextName,
                       @Nonnull Locale locale,
                       @Nonnull ChargingId chargingId) {
            this.contextName = contextName;
            this.locale = locale;
            this.chargingId = chargingId;
        }

        public ContextData.Builder serviceId(final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }
        public ContextData.Builder packageType(final String packageType) {
            this.packageType = packageType;
            return this;
        }
        public ContextData.Builder vendorId(final String vendorId) {
            this.vendorId = vendorId;
            return this;
        }
        public ContextData.Builder clientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }
        public ContextData.Builder kycCheck(final boolean kycCheck) {
            this.kycCheck = kycCheck;
            return this;
        }

        public ContextData build() {
            return new ContextData(this);
        }

    }

}
