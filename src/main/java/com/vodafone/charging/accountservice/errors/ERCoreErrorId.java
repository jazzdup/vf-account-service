package com.vodafone.charging.accountservice.errors;

/**
 * These are ER Core client ErrorIds which we use in this application.
 */
public enum ERCoreErrorId {

    SYSTEM_ERROR("SYSTEM_ERROR");

    private String value;

    ERCoreErrorId(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
