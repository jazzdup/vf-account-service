package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.exception.ExternalServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ExternalServiceCallSupplierTest {

    @Mock
    private Supplier<ResponseEntity> supplier;
    @Mock
    private ResponseEntity responseEntity;

    @InjectMocks
    private ExternalServiceCallSupplier externalServiceCallSupplier;

    @Test
    public void shouldExecuteSupplierAndRespondSuccessfully() {

        given(supplier.get()).willReturn(responseEntity);

        final ResponseEntity responseEntity = externalServiceCallSupplier.call(supplier).get();
        assertThat(responseEntity).isNotNull();
    }

    @Test
    public void shouldHandleClientExceptionWhenSupplierExecuted() {
        given(supplier.get()).willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> externalServiceCallSupplier.call(supplier).get())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageStartingWith("Client Exception")
                .hasCauseInstanceOf(HttpClientErrorException.class);
    }

    @Test
    public void shouldHandleServerExceptionWhenSupplierExecuted() {
        given(supplier.get()).willThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> externalServiceCallSupplier.call(supplier).get())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageStartingWith("Server Exception")
                .hasCauseInstanceOf(HttpServerErrorException.class);
    }

    @Test
    public void shouldHandleUnknownHttpExceptionWhenSupplierExecuted() {
        given(supplier.get()).willThrow(new UnknownHttpStatusCodeException(999, "statusText", new HttpHeaders(), null, null));

        assertThatThrownBy(() -> externalServiceCallSupplier.call(supplier).get())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageStartingWith("Unknown Http Exception")
                .hasCauseInstanceOf(UnknownHttpStatusCodeException.class);
    }

    @Test
    public void shouldHandleUnknownExceptionWhenSupplierExecuted() {
        given(supplier.get()).willThrow(new RuntimeException());

        assertThatThrownBy(() -> externalServiceCallSupplier.call(supplier).get())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageStartingWith("Unexpected Exception")
                .hasCauseInstanceOf(RuntimeException.class);
    }

}
