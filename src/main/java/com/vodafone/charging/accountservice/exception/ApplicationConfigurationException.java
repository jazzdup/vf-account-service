package com.vodafone.charging.accountservice.exception;

/**
 * For when there is a misconfiguration or properties cannot be accessed.
 */
public class ApplicationConfigurationException extends RuntimeException {

    public ApplicationConfigurationException(String message) {
        super(message);
    }

    public ApplicationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
