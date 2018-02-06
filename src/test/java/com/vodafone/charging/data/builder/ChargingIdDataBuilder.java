package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.data.object.NullableChargingId;

import java.util.Random;

/**
 * Represents an Id that uniquely identifies a Vodafone Customer Account
 */
public class ChargingIdDataBuilder {

    public static ChargingId aChargingId() {
        return new ChargingId(ChargingId.Type.MSISDN, generateChargingId());
    }

    public static ChargingId aChargingId(String msisdn) {
        return new ChargingId.Builder().type(ChargingId.Type.MSISDN).value(msisdn).build();
    }

    public static ChargingId aNullableChargingId(String type, String msisdn) {
        return new NullableChargingId(type, msisdn);
    }

    public static String generateChargingId() {
        return String.valueOf(new Random().nextInt());
    }
}
