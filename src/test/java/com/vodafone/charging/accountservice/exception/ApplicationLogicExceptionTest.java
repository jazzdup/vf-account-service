package com.vodafone.charging.accountservice.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationLogicExceptionTest {

    private ApplicationLogicException appException;

    @Test
    public void shouldCreateCustomException() {
        String message = "This is a test message";
        appException = new ApplicationLogicException(message);
        assertThat(appException.getMessage()).isEqualTo(message);
        assertThat(appException.getCause()).isNull();
    }

    @Test
    public void shouldCreateCustomExceptionWithThrowable() {
        String message = "This is a test message";
        String causeMessage = "This is an exception cause test message";
        appException = new ApplicationLogicException(message, new IllegalArgumentException(causeMessage));
        assertThat(appException.getMessage()).isEqualTo(message);
        assertThat(appException.getCause()).isInstanceOf(IllegalArgumentException.class);
        assertThat(appException.getCause().getMessage()).isEqualTo(causeMessage);

    }

}
