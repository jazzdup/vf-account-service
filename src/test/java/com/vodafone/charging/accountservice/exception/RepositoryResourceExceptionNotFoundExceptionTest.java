package com.vodafone.charging.accountservice.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RepositoryResourceExceptionNotFoundExceptionTest {

    @Test
    public void shouldCreateNewInstanceCorrectly() {

        final String message = "This is a test exception";
        final RuntimeException cause = new RuntimeException("This is a cause");

        assertThat(new RepositoryResourceNotFoundException(message))
                .isInstanceOf(RepositoryResourceNotFoundException.class)
                .hasMessage(message)
                .hasNoCause();
        assertThat(new RepositoryResourceNotFoundException(message, cause))
                .isInstanceOf(RepositoryResourceNotFoundException.class)
                .hasMessage(message)
                .hasCauseExactlyInstanceOf(cause.getClass());

    }

}
