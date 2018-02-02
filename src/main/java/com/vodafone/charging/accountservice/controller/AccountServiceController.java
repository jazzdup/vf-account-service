package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.exception.MethodArgumentValidationException;
import com.vodafone.charging.accountservice.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.google.common.base.Preconditions.checkArgument;
import static com.vodafone.charging.accountservice.domain.ValidateHttpHeaders.COUNTRY_HEADER_NAME;
import static com.vodafone.charging.accountservice.domain.ValidateHttpHeaders.TARGET_HEADER_NAME;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Spring Rest Controller to glue rest calls to the application logic.
 */
@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountServiceController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<EnrichedAccountInfo> enrichAccountData(@RequestHeader HttpHeaders headers,
                                                                 @Valid @RequestBody ContextData contextData) {

        this.checkMandatoryHeaders(headers);
        this.checkContextData(contextData);

        EnrichedAccountInfo accountInfo;
        try {
            accountInfo = accountService.enrichAccountData(contextData);
        } catch (Exception e) {
            throw new ApplicationLogicException(e.getMessage(), e);
        }
        return ResponseEntity.ok(accountInfo);
    }

    /*
    jsr303 Validation does not appear to work for the ChargingId object within contextInfo.
    Hence this is manually checked here.
     */
    public void checkContextData(final ContextData contextInfo) {
        try {
            checkArgument(isNotEmpty(contextInfo.getChargingId().getValue()), "chargingId.value is compulsory but was empty");
            checkArgument(isNotEmpty(contextInfo.getChargingId().getType()), "chargingId.type is compulsory but was empty");
        } catch (IllegalArgumentException iae) {
            throw new MethodArgumentValidationException(iae.getMessage(), iae);
        }
    }

    public void checkMandatoryHeaders(final HttpHeaders headers) {
        try {
            final String countryMessage = "header: " + COUNTRY_HEADER_NAME + " is mandatory";
            final String targetMessage = "header: " + TARGET_HEADER_NAME + " is mandatory";

            checkArgument(headers.get(COUNTRY_HEADER_NAME) != null, countryMessage);
            checkArgument(headers.get(TARGET_HEADER_NAME) != null, targetMessage);

            checkArgument( isNotEmpty(headers.get(COUNTRY_HEADER_NAME).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(countryMessage))),
                    countryMessage);

            checkArgument(isNotEmpty(headers.get(TARGET_HEADER_NAME).stream()
                    .findFirst().orElseThrow(() -> new IllegalArgumentException(targetMessage))), targetMessage);

//            checkArgument(isNotEmpty(country), countryMessage);
//            checkArgument(isNotEmpty(target), targetMessage);

        } catch (IllegalArgumentException iae) {
            throw new MethodArgumentValidationException(iae.getMessage(), iae);
        }

    }

}
