package com.vodafone.charging.data;

public enum ApplicationPortsEnum {
    DEFAULT_ER_IF_PORT(8458),
    DEFAULT_ER_CORE_PORT(8094),
    WILDFLY_MANAGEMENT_PORT(9094);

    private Integer value;

    ApplicationPortsEnum(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
