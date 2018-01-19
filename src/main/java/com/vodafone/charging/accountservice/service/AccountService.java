package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.domain.MessageControl;
import com.vodafone.charging.accountservice.domain.Routable;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
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

    //@TODO: make DI consistent across classes but causes AccountServiceTest to fail
    @Autowired
    private ERIFClient erifClient;

//    public AccountService(ERIFClient erifClient) {
//        this.erifClient = erifClient;
//    }

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {

        log.debug("AccountService.enrichAccountData, contextData={}", contextData );

        //convert context data to msgcontrol and routable objects
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate.name(), contextData);

        EnrichedAccountInfo enrichedAccountInfo = erifClient.validate(messageControl, routable);

        return enrichedAccountInfo;

    }

}
