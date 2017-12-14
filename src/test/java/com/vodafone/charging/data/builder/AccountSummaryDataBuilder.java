package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.AccountSummary;
import com.vodafone.charging.accountservice.domain.ChargingId;

import java.util.Locale;
import java.util.Random;

import static com.vodafone.charging.accountservice.domain.ChargingId.Type;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.*;

/**
 * Convenient way to build an AccountSummary for tests
 */
public class AccountSummaryDataBuilder {

    public static AccountSummary aAccount() {
        return new AccountSummary(generateAccountId(),
                Locale.UK,
                new ChargingId.Builder()
                        .type(Type.MSISDN)
                        .value(generateChargingId()).build());
    }

    public static AccountSummary aAccountWithNullAccountId() {
        return new AccountSummary(null,
                Locale.UK,
                new ChargingId.Builder()
                        .type(Type.MSISDN)
                        .value(generateChargingId()).build());
    }

    private static String generateAccountId() {
        return String.valueOf(new Random().nextInt());
    }

}
