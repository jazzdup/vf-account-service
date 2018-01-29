package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.vodafone.charging.accountservice.controller.AccountServiceControllerAdvice.ApplicationErrors.*;

@ControllerAdvice
public class AccountServiceControllerAdvice extends ResponseEntityExceptionHandler {

    public enum ApplicationErrors {

        APPLICATION_LOGIC_ERROR("SYSTEM_ERROR", "An error occurred within the Account Service.");

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
    public ResponseEntity<?> handleControllerException(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        return new ResponseEntity<>(new AccountServiceError("ERROR",
                APPLICATION_LOGIC_ERROR.errorId(),
                APPLICATION_LOGIC_ERROR.errorDesciption()), status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
