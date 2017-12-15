package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {

        //TODO call IF api to call ER IF and anywhere else required.

        throw new UnsupportedOperationException();
    }
}
