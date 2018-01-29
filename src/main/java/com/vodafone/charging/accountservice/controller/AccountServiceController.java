package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Spring Rest Controller to glue rest calls to the application logic.
 */
@RestController
@RequestMapping("/accounts")
public class AccountServiceController {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<EnrichedAccountInfo> enrichAccountData(@RequestBody ContextData contextData) {
        try {
            this.checkContextData(contextData);
        } catch (IllegalArgumentException iae) {
            log.error("Bad request. Mandatory Content is not provided in request body.", iae);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(APPLICATION_JSON_UTF8)
                    .build();
        }

        EnrichedAccountInfo accountInfo;
        try {
            accountInfo = accountService.enrichAccountData(contextData);
        } catch (Exception e) {
            throw new ApplicationLogicException(e.getMessage(), e);
        }
        return ResponseEntity.ok(accountInfo);
    }

    public void checkContextData(final ContextData contextInfo) {
        checkArgument(contextInfo != null, "value contextName was expected but was empty.");
        checkArgument(isNotEmpty(contextInfo.getContextName()), "value contextName was expected but was empty.");
        checkArgument(isNotEmpty(contextInfo.getChargingId().getValue()), "value chargingId.value was expected but was empty.");
        checkArgument(isNotEmpty(contextInfo.getChargingId().getType()), "value chargingId.type was expected but was empty");
    }

}
