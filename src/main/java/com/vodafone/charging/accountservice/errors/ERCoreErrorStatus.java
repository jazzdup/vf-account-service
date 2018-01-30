package com.vodafone.charging.accountservice.errors;

/**
 * Error status' that the ER Core client application stipulates
 */
public enum ERCoreErrorStatus {

    ERROR("ERROR"),
    DENIED("DENIED"),
    REJECTED("REJECTED");

    private String value;

    ERCoreErrorStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
