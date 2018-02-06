package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The main service object which routes logic
 * to more specific application services.
 */
@Service
@Slf4j
public class AccountService {

    @Autowired
    private ERIFClient erifClient;

    public AccountService() {
    }

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {

        log.debug("contextData={}", contextData);

//        final MessageControl messageControl = new MessageControl(contextData.getLocale());
//        final Routable routable = new Routable(RoutableType.validate, contextData);

//        return erifClient.validate(messageControl, routable);
        return erifClient.validate(contextData);

    }

}
