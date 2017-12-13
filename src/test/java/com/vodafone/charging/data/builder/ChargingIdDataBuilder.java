package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;

import java.util.Random;

/**
 * Represents an Id that uniquely identifies a Vodafone Customer Account
 */
public class ChargingIdDataBuilder {

    public static ChargingId aChargingId() {
        return new ChargingId(ChargingId.Type.MSISDN, generateChargingId());
    }

    public static String generateChargingId() {
        return String.valueOf(new Random().nextInt());
    }
}
