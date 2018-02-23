package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.exception.MethodArgumentValidationException;
import com.vodafone.charging.accountservice.exception.ServiceCallerSupplier;
import com.vodafone.charging.accountservice.service.AccountService;
import com.vodafone.charging.accountservice.service.SpendLimitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.HttpMethod;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Spring Rest Controller to glue rest calls to the application logic.
 */
@Api
@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountServiceController {

    private AccountService accountService;
    private SpendLimitService spendLimitService;
    private ServiceCallerSupplier serviceCallerSupplier;

    @Autowired
    public AccountServiceController(AccountService accountService,
                                    SpendLimitService spendLimitService,
                                    ServiceCallerSupplier serviceCallerSupplier) {
        this.accountService = accountService;
        this.spendLimitService = spendLimitService;
        this.serviceCallerSupplier = serviceCallerSupplier;
    }

    @ApiResponses({@ApiResponse(code = 500, message = "Internal Server Error", response = AccountServiceError.class),
            @ApiResponse(code = 400, message = "Bad Request", response = AccountServiceError.class)})
    @ApiOperation(value = "Obtain enriched charging account information",
            notes = "If you provide some contextual information this operation will process the request and respond with enriched charging account data.  " +
                    "\n In particular properties such as usergroups, customer type, billing account number will be returned ",
            response = EnrichedAccountInfo.class, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            httpMethod = javax.ws.rs.HttpMethod.POST, nickname = "enrichAccountData")

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<EnrichedAccountInfo> enrichAccountData(@RequestHeader HttpHeaders headers,
                                                                 @Valid @RequestBody ContextData contextData) {
        this.checkContextData(contextData);
        final EnrichedAccountInfo accountInfo = serviceCallerSupplier
                .wrap(() -> accountService.enrichAccountData(contextData)).get();

        return ResponseEntity.ok(accountInfo);
    }

    @ApiResponses({@ApiResponse(code = 500, message = "Internal Server Error", response = AccountServiceError.class),
            @ApiResponse(code = 400, message = "Bad Request", response = AccountServiceError.class)})
    @ApiOperation(value = "Get Account",
            notes = "Get Account using ChargingId",
            response = Account.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            httpMethod = HttpMethod.GET, nickname = "getAccount")
    @RequestMapping(path = "/{chargingIdType}/{chargingIdValue}", method = GET,
            produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<Account> getAccount(@PathVariable String chargingIdType, @PathVariable String chargingIdValue) {

        final Optional<ChargingId> chargingIdOpt = ChargingId.fromString(chargingIdType, chargingIdValue);
        final ChargingId chargingId = chargingIdOpt
                .orElseThrow(() -> new MethodArgumentValidationException("Incorrect ChargingIdType or ChargingIdValue in request"));

        Account account;
        try {
            account = accountService.getAccount(chargingId);
        } catch (Exception e) {
            throw new ApplicationLogicException(e.getMessage(), e);
        }
        return ResponseEntity.ok(account);
    }

    @ApiResponses({@ApiResponse(code = 500, message = "Internal Server Error", response = AccountServiceError.class),
            @ApiResponse(code = 400, message = "Bad Request", response = AccountServiceError.class)})
    @ApiOperation(value = "Get UserGroups for an Account",
            notes = "Get Account using AccountId",
            response = Account.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            httpMethod = HttpMethod.GET, nickname = "getUserGroups")
    @RequestMapping(path = "/{accountId}", method = GET,
            produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<Account> getAccount(@PathVariable String accountId) {

        Account account;
        try {
            account = accountService.getAccount(accountId);
        } catch (Exception e) {
            throw new ApplicationLogicException(e.getMessage(), e);
        }
        return ResponseEntity.ok(account);
    }

    @ApiResponses({@ApiResponse(code = 500, message = "Internal Server Error", response = AccountServiceError.class),
            @ApiResponse(code = 400, message = "Bad Request", response = AccountServiceError.class)})
    @ApiOperation(value = "Get UserGroups for an Account",
            notes = "Get Usergroups for an Account",
            response = Account.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            httpMethod = HttpMethod.GET, nickname = "getUserGroups")
    @RequestMapping(path = "/{accountId}/profile/usergroups", method = GET,
            produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getUserGroups(@PathVariable String accountId) {
        List<String> userGroups;
        try {
            userGroups = accountService.getUserGroups(accountId);
        } catch (Exception e) {
            throw new ApplicationLogicException(e.getMessage(), e);
        }
        return ResponseEntity.ok(userGroups);
    }


    @RequestMapping(path = "/{accountId}/profile/spendlimits", method = POST,
            consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE},
            produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> putAccountSpentLimit(@PathVariable String accountId,
                                                       @Valid @RequestBody List<SpendLimitInfo> spendLimitsInfo) {

        final Account account = serviceCallerSupplier.wrap(() ->
                spendLimitService.updateSpendLimits(accountId, spendLimitsInfo)).get();

        return ResponseEntity.created(URI.create(accountId + "/profile/spendlimits/"))
                .body(account);
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
}
