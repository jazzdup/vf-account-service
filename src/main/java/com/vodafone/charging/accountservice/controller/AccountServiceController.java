package com.vodafone.charging.accountservice.controller;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.exception.BadRequestException;
import com.vodafone.charging.accountservice.service.AccountValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/accounts")
public class AccountServiceController {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceController.class);

    @Autowired
    private AccountValidationService accountValidationService;

    @RequestMapping(method = POST)
    public ResponseEntity<EnrichedAccountInfo> enrichAccountData(@RequestBody ContextData contextData) {
        checkContextData(contextData);
//        accountValidationService.validateChargingId(contextData);

        return ResponseEntity.ok(getAccountInfo());
    }

    //TODO Get a mock to return this!
    private EnrichedAccountInfo getAccountInfo() {
        return new EnrichedAccountInfo.Builder("OK")
                .usergroups(Lists.newArrayList("user-group1", "user-group2"))
                .ban("123456_ban")
                .billingCycleDay(1)
                .serviceProviderId("serviceProviderId")
                .childServiceProviderId("childServiceProviderId")
                .serviceProviderType("serviceProviderType")
                .errorId("test-error-id")
                .errorDescription("test-error-description")
                .build();
    }

    private void checkContextData(ContextData contextInfo) {
        try {
//            checkNotNull(contextInfo);
            checkArgument(contextInfo != null, "value contextName was expected but was empty.");
            checkArgument(isNotEmpty(contextInfo.getContextName()), "value contextName was expected but was empty.");
            checkArgument(isNotEmpty(contextInfo.getChargingId().getValue()), "value chargingId.value was expected but was empty.");
            checkArgument(isNotEmpty(contextInfo.getChargingId().getType().type()), "value chargingId.type was expected but was empty");

        } catch (IllegalArgumentException iae) {
            log.error("Mandatory Content is not provided in request body.");
            throw new BadRequestException("Mandatory Content is not provided in request body", iae);
        }
    }
}
