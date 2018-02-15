package com.vodafone.charging.accountservice.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Getter

public class Routable {
    private String type; //i.e. validate
    private ChargingId chargingId;
    private String clientId; //e.g. demo
    private Boolean kycCheck;

    public Routable(RoutableType type, @NonNull ContextData contextData) {
        this.type = type.name();
        this.chargingId = contextData.getChargingId();
        this.clientId = contextData.getClientId();
        this.kycCheck = contextData.isKycCheck();
    }
}
