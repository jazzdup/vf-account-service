package com.vodafone.charging.accountservice.domain.model;

import com.vodafone.charging.accountservice.domain.ChargingId;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class Account {

    private String accountId;
    private ChargingId chargingId;
    private Date lastValidate;

}
