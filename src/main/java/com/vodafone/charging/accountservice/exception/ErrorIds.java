package com.vodafone.charging.accountservice.exception;

/**
 * Represents an internal application ErrorId and ErrorDescription which can be passed back to the client.
 */
public enum ErrorIds {

    VAS_INTERNAL_SERVER_ERROR("VAS Internal Server Error", "Internal Server Error in Vodafone Account Service.");

    private final String errorId;
    private final String errorDescription;

    ErrorIds(String errorId, String errorDescription) {
        this.errorId = errorId;
        this.errorDescription = errorDescription;
    }

    public String errorId() {
        return errorId;
    }

    public String errorDescription() {
        return errorDescription;
    }
}
