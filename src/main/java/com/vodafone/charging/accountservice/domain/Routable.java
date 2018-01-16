package com.vodafone.charging.accountservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Getter
@Builder
public class Routable {
    private String type; //i.e. validate
    private ChargingId chargingId;
    private String clientId; //e.g. demo
    private Boolean kycCheck;

    public Routable(String type, ChargingId chargingId, String clientId, Boolean kycCheck) {
        this.type = type;
        this.chargingId = chargingId;
        this.clientId = clientId;
        this.kycCheck = kycCheck;
    }
}
