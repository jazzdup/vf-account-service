package com.vodafone.charging.accountservice.errors;

import static com.vodafone.charging.accountservice.errors.ERCoreErrorId.*;
import static com.vodafone.charging.accountservice.errors.ERCoreErrorStatus.*;

/**
 * These are error code mappings assuming ER Core is the client
 */
public enum ApplicationErrors {

    APPLICATION_LOGIC_ERROR(ERROR, SYSTEM_ERROR, "An error occurred within the Account Service."),
    REPOSITORY_RESOURCE_NOT_FOUND_ERROR(ERROR, CLIENT_ERROR , "Could not retrieve repository resource using identifier."),
    MESSAGE_NOT_READABLE_ERROR(ERROR, SYSTEM_ERROR, "Could not read incoming message."),
    BAD_REQUEST_ERROR(ERROR, SYSTEM_ERROR, "Incorrect request parameters were passed."),
    EXTERNAL_SERVICE_ERROR(ERROR, SYSTEM_ERROR , "Error received communicating with an external system."),
    UNKNOWN_ERROR(ERROR, SYSTEM_ERROR, "An unknown error has occurred.");

    private ERCoreErrorStatus status;
    private ERCoreErrorId errorId;
    private String errorDescription;

    ApplicationErrors(ERCoreErrorStatus status, ERCoreErrorId errorId, String errorDescription) {
        this.status = status;
        this.errorId = errorId;
        this.errorDescription = errorDescription;
    }

    public ERCoreErrorStatus status() {
        return status;
    }

    public ERCoreErrorId errorId() {
        return errorId;
    }

    public String errorDesciption() {
        return errorDescription;
    }
}