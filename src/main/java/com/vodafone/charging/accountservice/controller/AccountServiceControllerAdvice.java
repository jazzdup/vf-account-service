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

import static com.vodafone.charging.accountservice.controller.AccountServiceControllerAdvice.ApplicationErrors.APPLICATION_LOGIC_ERROR;
import static com.vodafone.charging.accountservice.controller.AccountServiceControllerAdvice.ApplicationErrors.BAD_REQUEST_ERROR;
import static com.vodafone.charging.accountservice.controller.AccountServiceControllerAdvice.ApplicationErrors.UNKNOWN_ERROR;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AccountServiceControllerAdvice extends ResponseEntityExceptionHandler {

    private Logger log = LoggerFactory.getLogger(AccountServiceControllerAdvice.class);

    public enum ApplicationErrors {

        APPLICATION_LOGIC_ERROR("SYSTEM_ERROR", "An error occurred within the Account Service."),
        BAD_REQUEST_ERROR("SYSTEM_ERROR", "Incorrect request parameters were passed."),
        UNKNOWN_ERROR("SYSTEM_ERROR", "An unknown error has occurred.");

        private String errorId;
        private String errorDesciption;

        ApplicationErrors(String errorId, String errorDesciption) {
            this.errorId = errorId;
            this.errorDesciption = errorDesciption;
        }

        public String errorId() {
            return errorId;
        }

        public String errorDesciption() {
            return errorDesciption;
        }
    }

    @ExceptionHandler(ApplicationLogicException.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleApplicationLogicException(HttpServletRequest request, ApplicationLogicException ex) {
        log.error("Handling ApplicationLogicException with message: {}", ex.getMessage());
        final HttpStatus status = this.getStatus(request, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(new AccountServiceError("ERROR",
                APPLICATION_LOGIC_ERROR.errorId(),
                APPLICATION_LOGIC_ERROR.errorDesciption()), status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        log.error("Handling IllegalArgumentException with message: {}", ex.getMessage());
        return new ResponseEntity<>(new AccountServiceError("ERROR",
                BAD_REQUEST_ERROR.errorId(),
                BAD_REQUEST_ERROR.errorDesciption()), HttpStatus.BAD_REQUEST);
    }

    //Catch everything not mapped yet
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleGenericException(Exception ex, HttpStatus status) {
        log.error("Handling IllegalArgumentException with message: {}", ex.getMessage());
        return new ResponseEntity<>(new AccountServiceError("ERROR",
                UNKNOWN_ERROR.errorId(),
                UNKNOWN_ERROR.errorDesciption()), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Handling IllegalArgumentException with message: {}", ex.getMessage());
        return new ResponseEntity<>(new AccountServiceError("ERROR",
                BAD_REQUEST_ERROR.errorId(),
                BAD_REQUEST_ERROR.errorDesciption()), status);
    }

    private HttpStatus getStatus(HttpServletRequest request, HttpStatus defaultStatus) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return defaultStatus;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
