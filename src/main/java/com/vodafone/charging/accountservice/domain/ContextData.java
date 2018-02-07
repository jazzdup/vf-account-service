package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.ERIFRequestTarget;
import com.vodafone.charging.accountservice.domain.enums.PackageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents operation context
 */
@Component
@ApiModel(description = "Contextual Information from incoming requests")
public class ContextData {
    private String contextName;

    @ApiModelProperty(value = "This the locale of the customer", required = true)
    @NotNull(message = "'locale' is compulsory and cannot be null")
    private Locale locale;

    @ApiModelProperty(value = "Vodafone Charging Account Type", required = true)
    @NotNull(message = "'chargingId' is compulsory and cannot be null")
    private ChargingId chargingId;

    private String serviceId;
    private PackageType packageType;
    private String partnerId;
    private String vendorId;
    private String clientId;
    private boolean kycCheck;
    private ERIFRequestTarget target;

    protected ContextData() {
    }

    private ContextData(final Builder builder) {
        this.contextName = builder.contextName;
        this.locale = builder.locale;
        this.chargingId = builder.chargingId;
        this.serviceId = builder.serviceId;
        this.packageType = builder.packageType;
        this.vendorId = builder.vendorId;
        this.partnerId = builder.partnerId;
        this.clientId = builder.clientId;
        this.kycCheck = builder.kycCheck;
        this.target = builder.target;
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

    public String getPartnerId() {
        return partnerId;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isKycCheck() {
        return kycCheck;
    }

    public ERIFRequestTarget getTarget() {
        return target;
    }

    public Map<String, Object> asMap() throws IllegalAccessException {
        Map<String, Object> values = new HashMap<>();

        Field[] fieldsArr = this.getClass().getDeclaredFields();

        for (Field field : fieldsArr) {
            values.put(field.getName(), field.get(this));
        }
        return values;
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
                ", partnerId='" + partnerId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", kycCheck=" + kycCheck +
                ", target=" + target +
                '}';
    }

    public static class Builder {

        private String contextName;
        private final Locale locale;
        private final ChargingId chargingId;
        private String serviceId;
        private PackageType packageType;
        private String vendorId;
        private String partnerId;
        private String clientId;
        private boolean kycCheck;
        private ERIFRequestTarget target = ERIFRequestTarget.LOCAL;

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

        public Builder partnerId(final String partnerId) {
            this.partnerId = partnerId;
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

        public Builder target(final ERIFRequestTarget target) {
            this.target = target;
            return this;
        }

        public ContextData build() {
            return new ContextData(this);
        }

    }
}

