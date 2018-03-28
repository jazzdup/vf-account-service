package com.vodafone.charging.accountservice.exception;

public class NullRestResponseReceivedException extends RuntimeException {

    public NullRestResponseReceivedException(final String message) {
        super(message);
    }
    public NullRestResponseReceivedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
