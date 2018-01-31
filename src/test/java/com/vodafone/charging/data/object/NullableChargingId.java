package com.vodafone.charging.data.object;

import com.vodafone.charging.accountservice.domain.ChargingId;

public class NullableChargingId extends ChargingId {

    private String type;
    private String value;

    public NullableChargingId(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
