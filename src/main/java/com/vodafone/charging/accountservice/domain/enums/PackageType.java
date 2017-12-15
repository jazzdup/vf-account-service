package com.vodafone.charging.accountservice.domain.enums;

public enum PackageType {

    EVENT("EVENT"),
    CALENDAR_PACKAGE_TYPE("CALENDAR"),
    EVENT_CALENDAR_PACKAGE_TYPE("BOTH");

    private String type;

    PackageType(String type) {
        this.type = type;
    }
}
