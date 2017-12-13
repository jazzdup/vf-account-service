package com.vodafone.charging.accountservice.controller;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.exception.BadRequestException;
import com.vodafone.charging.accountservice.model.Account;
import com.vodafone.charging.accountservice.model.Validation;
import com.vodafone.charging.accountservice.service.AccountValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/accounts/{accountId}")
public class AccountServiceController {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceController.class);

    @Autowired
    private AccountValidationService accountValidationService;

    @RequestMapping(path = "/validation", method = POST)
    public ResponseEntity<Validation> validate(@RequestBody Account account) {
        checkAccountData(account);
        log.debug("CALLING VALIDATE WITH ACCOUNTID: {}", account.getId());
//        accountValidationService.validateChargingId(account);

        return ResponseEntity.ok(getValidation());
    }

    private Validation getValidation() {
        return new Validation.Builder().usergroups(Lists.newArrayList("test-usergroup")).result(true).build();
    }

    private void checkAccountData(Account account) {
        if(account == null || account.getId() == null || account.getLocale() == null) {
            throw new BadRequestException("Incorrect Body provided in request");
        }
    }

}
