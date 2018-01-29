package com.vodafone.charging.accountservice.exception;

public class AccountServiceError {

    private String status;
    private String errorId;
    private String errorDescription;

    private AccountServiceError() {
    }

    public AccountServiceError(String status, String errorId, String errorDescription) {
        this.status = status;
        this.errorId = errorId;
        this.errorDescription = errorDescription;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
