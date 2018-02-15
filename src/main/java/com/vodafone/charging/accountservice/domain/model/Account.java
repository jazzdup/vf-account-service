package com.vodafone.charging.accountservice.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class Account {

    private String accountId;
    private String chargingId;
    private Date lastValidate;

}
