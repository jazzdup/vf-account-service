package com.vodafone.charging.accountservice.exception;

/**
 * Exceptions with communicating with the ER Service endpoint.
 */
public class ERServiceException extends RuntimeException {

    public ERServiceException(final String message) {
        super(message);
    }
    public ERServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
