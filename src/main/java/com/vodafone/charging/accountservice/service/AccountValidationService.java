package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.Account;
import com.vodafone.charging.accountservice.domain.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountValidationService {

    private static final Logger log = LoggerFactory.getLogger(AccountValidationService.class);

    public Validation validateChargingId(Account account) {
        throw new UnsupportedOperationException();
    }
}
