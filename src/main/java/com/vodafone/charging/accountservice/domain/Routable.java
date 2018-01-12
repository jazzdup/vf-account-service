package com.vodafone.charging.accountservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @ToString @NoArgsConstructor
public class Routable {
    private String type; //i.e. validate
    private ChargingId chargingId;
    private String clientId; //e.g. demo
    private Boolean kycCheck;
}
