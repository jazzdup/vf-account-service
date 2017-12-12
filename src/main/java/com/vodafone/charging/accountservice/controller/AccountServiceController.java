package com.vodafone.charging.accountservice.controller;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.model.Account;
import com.vodafone.charging.accountservice.model.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/accounts/{accountId}")
public class AccountServiceController {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceController.class);

    @RequestMapping(path = "/validation", method = POST)
    public ResponseEntity<Validation> validate(@PathVariable String accountId, @RequestBody Account account) {


        //TODO call to get account info from ER IF
        log.debug("CALLING VALIDATE WITH ACCOUNTID: {}", accountId);
        return ResponseEntity.ok(getValidation());
    }


    private Validation getValidation() {
        return new Validation.Builder().usergroups(Lists.newArrayList("test-usergroup")).result(true).build();
    }

}
