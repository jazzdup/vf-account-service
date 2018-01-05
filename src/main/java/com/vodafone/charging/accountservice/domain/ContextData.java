package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.PackageType;
import lombok.NonNull;
import org.springframework.stereotype.Component;

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
    private String serviceId;
    private PackageType packageType;
    private String vendorId;
    private String clientId;
    private boolean kycCheck;

    private ContextData() {
    }

    private ContextData(final Builder builder) {
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

    @Override
    public String toString() {
        return "ContextData{" +
                "contextName='" + contextName + '\'' +
                ", locale=" + locale +
                ", chargingId=" + chargingId +
                ", serviceId='" + serviceId + '\'' +
                ", packageType=" + packageType +
                ", vendorId='" + vendorId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", kycCheck=" + kycCheck +
                '}';
    }

    public static class Builder {

        private String contextName;
        private final Locale locale;
        private final ChargingId chargingId;
        private String serviceId; //should be optional
        private PackageType packageType;
        private String vendorId; //optional
        private String clientId;
        private boolean kycCheck; //optional

        public Builder(@NonNull String contextName,
                       @NonNull Locale locale,
                       @NonNull ChargingId chargingId) {
            this.contextName = contextName;
            this.locale = locale;
            this.chargingId = chargingId;
        }

        public Builder serviceId(final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }
        public Builder packageType(final PackageType packageType) {
            this.packageType = packageType;
            return this;
        }
        public Builder vendorId(final String vendorId) {
            this.vendorId = vendorId;
            return this;
        }
        public Builder clientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }
        public Builder kycCheck(final boolean kycCheck) {
            this.kycCheck = kycCheck;
            return this;
        }

        public ContextData build() {
            return new ContextData(this);
        }

    }
}
