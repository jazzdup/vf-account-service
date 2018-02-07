package com.vodafone.charging.accountservice.exception;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;

public class NullRestResponseReceivedExceptionTest {

    @Test
    public void shouldCreateExceptionWithMessage() {
        final String message = "this is a test exception";
        NullRestResponseReceivedException ex = new NullRestResponseReceivedException(message);
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex).hasMessage(message);
    }
    @Test
    public void shouldCreateExceptionWithMessageAndCause() {
        final String message = "this is a test exception";
        final String causeMessage = "this is a test exception cause";
        final HttpClientErrorException clientEx = new HttpClientErrorException(HttpStatus.NOT_FOUND, causeMessage);
        NullRestResponseReceivedException ex = new NullRestResponseReceivedException(message, clientEx);
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex).hasMessage(message);
        assertThat(ex).hasCauseInstanceOf(HttpClientErrorException.class);
        assertThat(ex.getCause()).hasMessageContaining(causeMessage);
        assertThat(ex.getCause()).hasMessageContaining(String.valueOf(HttpStatus.NOT_FOUND.value()));
    }

}
