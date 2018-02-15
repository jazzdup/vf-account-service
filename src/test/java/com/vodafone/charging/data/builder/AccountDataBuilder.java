package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.model.Account;

import java.util.Date;
import java.util.Random;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.*;

public class AccountDataBuilder {

    public static Account aAccount() {
        return Account.builder().id(String.valueOf(new Random().nextInt()))
                .chargingId(aChargingId())
                .lastValidate(new Date()).build();
    }

}
