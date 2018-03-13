package com.vodafone.charging.accountservice.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExternalServiceExceptionTest {

    @Test
    public void shouldCreateNewInstanceCorrectly() {

        final String message = "This is a test exception";
        final RuntimeException cause = new RuntimeException("This is a cause");

        assertThat(new ExternalServiceException(message))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessage(message)
                .hasNoCause();
        assertThat(new ExternalServiceException(message, cause))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessage(message)
                .hasCauseExactlyInstanceOf(cause.getClass());

    }

}
