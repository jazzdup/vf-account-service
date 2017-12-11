package com.vodafone.charging.accountservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/accounts/{accountId}")
public class AccountServiceController {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceController.class);

    @RequestMapping(path = "/validation", method = POST)
    public ResponseEntity<?> validate(@PathVariable String accountId) {
        //TODO call to get account info from ER IF
        log.error("CALLING VALIDATE WITH ACCOUNTID: {}", accountId);
        return ResponseEntity.ok().build();
    }
}
