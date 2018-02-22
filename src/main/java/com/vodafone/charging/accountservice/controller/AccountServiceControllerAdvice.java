package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.exception.MethodArgumentValidationException;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.vodafone.charging.accountservice.errors.ApplicationErrors.*;
import static com.vodafone.charging.accountservice.errors.ERCoreErrorId.SYSTEM_ERROR;
import static com.vodafone.charging.accountservice.errors.ERCoreErrorStatus.ERROR;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Slf4j
public class AccountServiceControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationLogicException.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleApplicationLogicException(HttpServletRequest request,
                                                                               ApplicationLogicException ex) {
        log.error("Handling ApplicationLogicException with message: {}", ex.getMessage());
        return new ResponseEntity<>(AccountServiceError.builder()
                .status(APPLICATION_LOGIC_ERROR.status().value())
                .errorId(APPLICATION_LOGIC_ERROR.errorId().value())
                .errorDescription(APPLICATION_LOGIC_ERROR.errorDesciption()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RepositoryResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleApplicationLogicException(HttpServletRequest request,
                                                                               RepositoryResourceNotFoundException ex) {
        log.error("Handling RepositoryResourceNotFoundException with message: {}", ex.getMessage());
        return new ResponseEntity<>(AccountServiceError.builder()
                .status(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.status().value())
                .errorId(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.errorId().value())
                .errorDescription(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.errorDesciption() +
                        " message: "+ ex.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentValidationException.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleIllegalArgumentException(HttpServletRequest request,
                                                                              MethodArgumentValidationException ex) {
        return new ResponseEntity<>(AccountServiceError.builder()
                .status(ERROR.value())
                .errorId(SYSTEM_ERROR.value())
                .errorDescription(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);

    }

    //Catch everything not mapped yet
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<AccountServiceError> handleGenericException(HttpServletRequest request, Exception ex) {
        log.error("Handling IllegalArgumentException with message: {}", ex.getMessage());
        return new ResponseEntity<>(AccountServiceError
                .builder().status(UNKNOWN_ERROR.status().value())
                .errorId(UNKNOWN_ERROR.errorId().value())
                .errorDescription(UNKNOWN_ERROR.errorDesciption()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error("Incoming request message not readable: {}, returning status {}", ex.getMessage(), status.toString());
        return new ResponseEntity<>(AccountServiceError
                .builder().status(MESSAGE_NOT_READABLE_ERROR.status().value())
                .errorId(MESSAGE_NOT_READABLE_ERROR.errorId().value())
                .errorDescription(MESSAGE_NOT_READABLE_ERROR.errorDesciption()).build(), status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return new ResponseEntity<>(AccountServiceError
                .builder().status(ERROR.value())
                .errorId(SYSTEM_ERROR.value())
                .errorDescription(ex.getMessage()).build(), status);

    }
}
