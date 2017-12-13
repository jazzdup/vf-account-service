package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.model.Account;
import com.vodafone.charging.accountservice.model.ChargingId;

import java.util.Locale;
import java.util.Random;

import static com.vodafone.charging.accountservice.model.ChargingId.Type;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.*;

/**
 * Represents a Vodafone customer account
 */
public class AccountDataBuilder {

    public static Account aAccount() {
        return new Account(generateAccountId(),
                Locale.UK,
                new ChargingId.Builder()
                        .type(Type.MSISDN)
                        .value(generateChargingId()).build());
    }

    public static Account aAccountWithNullAccountId() {
        return new Account(null,
                Locale.UK,
                new ChargingId.Builder()
                        .type(Type.MSISDN)
                        .value(generateChargingId()).build());
    }

    private static String generateAccountId() {
        return String.valueOf(new Random().nextInt());
    }

}
