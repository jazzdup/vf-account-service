package com.vodafone.charging.mock;

public enum ApplicationPortsEnum {
    DEFAULT_ER_IF_PORT(8458);

    private Integer value;

    ApplicationPortsEnum(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
