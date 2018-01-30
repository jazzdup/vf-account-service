package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.vodafone.charging.accountservice.errors.ApplicationErrors.*;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AccountServiceControllerAdvice extends ResponseEntityExceptionHandler {

    private Logger log = LoggerFactory.getLogger(AccountServiceControllerAdvice.class);

    @ExceptionHandler(ApplicationLogicException.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleApplicationLogicException(HttpServletRequest request, ApplicationLogicException ex) {
        log.error("Handling ApplicationLogicException with message: {}", ex.getMessage());
        final HttpStatus status = this.getStatus(request, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(new AccountServiceError(APPLICATION_LOGIC_ERROR.status().value(),
                APPLICATION_LOGIC_ERROR.errorId().value(),
                APPLICATION_LOGIC_ERROR.errorDesciption()), status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        log.error("Handling IllegalArgumentException with message: {}", ex.getMessage());
        return new ResponseEntity<>(new AccountServiceError(BAD_REQUEST_ERROR.status().value(),
                BAD_REQUEST_ERROR.errorId().value(),
                BAD_REQUEST_ERROR.errorDesciption()), HttpStatus.BAD_REQUEST);
    }

    //Catch everything not mapped yet
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleGenericException(Exception ex, HttpStatus status) {
        log.error("Handling IllegalArgumentException with message: {}", ex.getMessage());
        return new ResponseEntity<>(new AccountServiceError(UNKNOWN_ERROR.status().value(),
                UNKNOWN_ERROR.errorId().value(),
                UNKNOWN_ERROR.errorDesciption()), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Handling IllegalArgumentException with message: {}", ex.getMessage());
        return new ResponseEntity<>(new AccountServiceError(MESSAGE_NOT_READABLE_ERROR.status().value(),
                MESSAGE_NOT_READABLE_ERROR.errorId().value(),
                MESSAGE_NOT_READABLE_ERROR.errorDesciption()), status);
    }

    private HttpStatus getStatus(HttpServletRequest request, HttpStatus defaultStatus) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return defaultStatus;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
