package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The main service object which routes logic
 * to more specific application services.
 */
@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {

        log.debug("AccountService.enrichAccountData, contextData={}", contextData );

        throw new UnsupportedOperationException();
    }
}
