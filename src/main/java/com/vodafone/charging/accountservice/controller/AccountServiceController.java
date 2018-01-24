package com.vodafone.charging.accountservice.controller;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
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
import static com.vodafone.charging.accountservice.exception.ErrorIds.VAS_INTERNAL_SERVER_ERROR;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

    @RequestMapping(method = POST)
    public ResponseEntity<EnrichedAccountInfo> enrichAccountData(@RequestBody ContextData contextData) {
        try {
            this.checkContextData(contextData);
        } catch (IllegalArgumentException iae) {
            log.error("Bad request. Mandatory Content is not provided in request body.", iae);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        EnrichedAccountInfo accountInfo;
        try {
            accountInfo = accountService.enrichAccountData(contextData);
        } catch (Exception e) {
//            return createSuccessResponse(contextData);
            return createResponse(e);
        }
        return ResponseEntity.ok(accountInfo);
    }

    public void checkContextData(final ContextData contextInfo) {
        checkArgument(contextInfo != null, "value contextName was expected but was empty.");
        checkArgument(isNotEmpty(contextInfo.getContextName()), "value contextName was expected but was empty.");
        checkArgument(isNotEmpty(contextInfo.getChargingId().getValue()), "value chargingId.value was expected but was empty.");
        checkArgument(isNotEmpty(contextInfo.getChargingId().getType()), "value chargingId.type was expected but was empty");
    }

    public ResponseEntity<EnrichedAccountInfo> createResponse(Exception e) {
        //TODO this should be moved to an http error mapper.  502 Bad Gateway for IF being down
        //
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body(new EnrichedAccountInfo.Builder("ERROR")
                        .errorId(VAS_INTERNAL_SERVER_ERROR.errorId())
                        .errorDescription(VAS_INTERNAL_SERVER_ERROR.errorDescription()).build());
    }

    //This is for testing purposes only
    private ResponseEntity<EnrichedAccountInfo> createSuccessResponse(ContextData data) {

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(new EnrichedAccountInfo.Builder("ACCEPTED")
                        .ban(data.getChargingId().getValue() + "_BAN")
                        .serviceProviderId("test-spid")
                        .serviceProviderType("test-service-provider-type")
                        .usergroups(Lists.newArrayList("usergroup1", "usergroup2"))
                        .childServiceProviderId("child-service-provider-id")
                        .isPrepay("PRE")
                        .billingCycleDay(3)
//                        .errorDescription("OK")
//                        .errorId("OK")
                        .build());
    }

}
