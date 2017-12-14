package com.vodafone.charging.accountservice.controller;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.exception.BadRequestException;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountData;
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
@RequestMapping("/accounts")
public class AccountServiceController {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceController.class);

    @Autowired
    private AccountValidationService accountValidationService;

    @RequestMapping(method = POST)
    public ResponseEntity<EnrichedAccountData> enrichAccountData(@RequestBody ContextData contextData) {
        checkAccountInfo(contextData);
//        accountValidationService.validateChargingId(account);

        return ResponseEntity.ok(getCustomerInfo());
    }

    private EnrichedAccountData getCustomerInfo() {
        return new EnrichedAccountData.Builder().usergroups(Lists.newArrayList("test-usergroup")).result(true).build();
    }

    private void checkAccountInfo(ContextData contextData) {
        if(contextData == null || contextData.getId() == null || contextData.getLocale() == null) {
            throw new BadRequestException("Incorrect Body provided in request");
        }
    }

}
