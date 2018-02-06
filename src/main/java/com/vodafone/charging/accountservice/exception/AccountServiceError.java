package com.vodafone.charging.accountservice.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountServiceError {
    private String status;
    private String errorId;
    private String errorDescription;
}
