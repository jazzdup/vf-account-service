package com.vodafone.charging.accountservice.exception;

import lombok.Getter;

@Getter
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
}
