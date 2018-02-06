package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.exception.MethodArgumentValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import static com.vodafone.charging.accountservice.errors.ApplicationErrors.*;
import static com.vodafone.charging.accountservice.errors.ERCoreErrorId.SYSTEM_ERROR;
import static com.vodafone.charging.accountservice.errors.ERCoreErrorStatus.ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceControllerAdviceTest {

    @Mock
    private HttpHeaders httpHeaders;
    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private AccountServiceControllerAdvice advice;

    @Before
    public void init() {
        initMocks(this);
    }

    @Test
    public void shouldHandleApplicationLogicExceptions() {
        final AccountServiceError expectedError = AccountServiceError.builder()
                .status(ERROR.value())
                .errorId(SYSTEM_ERROR.value())
                .errorDescription(APPLICATION_LOGIC_ERROR.errorDesciption())
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();

        final ResponseEntity<AccountServiceError> response = advice.handleApplicationLogicException(request,
                new ApplicationLogicException("This is a test ApplicationLogicException"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualToComparingFieldByField(expectedError);
    }

    @Test
    public void shouldHandleMethodArgumentValidationException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String message = "This is a test ApplicationLogicException";

        final ResponseEntity<AccountServiceError> response = advice.handleIllegalArgumentException(request,
                new MethodArgumentValidationException(message));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(ERROR.value());
        assertThat(response.getBody().getErrorId()).isEqualTo(SYSTEM_ERROR.value());
        assertThat(response.getBody().getErrorDescription()).isEqualTo(message);
    }

    @Test
    public void shouldHandleGenericException() {
        final AccountServiceError expectedError = AccountServiceError.builder()
                .status(ERROR.value())
                .errorId(SYSTEM_ERROR.value())
                .errorDescription(UNKNOWN_ERROR.errorDesciption())
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();

        final ResponseEntity<AccountServiceError> response = advice.handleGenericException(request,
                new ApplicationLogicException("This is a test ApplicationLogicException"));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualToComparingFieldByField(expectedError);
    }

    @Test
    public void shouldHandleHttpMessageNotReadableException() throws Exception {
        final AccountServiceError expectedError = AccountServiceError.builder()
                .status(ERROR.value())
                .errorId(SYSTEM_ERROR.value())
                .errorDescription(MESSAGE_NOT_READABLE_ERROR.errorDesciption())
                .build();

        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        final ResponseEntity<Object> response =
                advice.handleHttpMessageNotReadable(ex, httpHeaders, HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualToComparingFieldByField(expectedError);
    }

    @Test
    public void shouldHandleMethodArgumentNotValid() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        final String message = "This is a test exception message";
        given(ex.getMessage()).willReturn(message);

        final AccountServiceError expectedError = AccountServiceError.builder()
                .status(ERROR.value())
                .errorId(SYSTEM_ERROR.value())
                .errorDescription(message)
                .build();

        final ResponseEntity<Object> response =
                advice.handleMethodArgumentNotValid(ex, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualToComparingFieldByField(expectedError);
    }

}
