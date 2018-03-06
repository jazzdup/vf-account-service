package com.vodafone.charging.accountservice.exception;

/**
 * Exceptions with communicating with an External Service endpoint.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(final String message) {
        super(message);
    }
    public ExternalServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
