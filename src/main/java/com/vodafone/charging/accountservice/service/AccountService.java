package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * The main service object which routes logic
 * to more specific application services.
 */
@Service
@Slf4j
public class AccountService {

    @Autowired
    private ERIFClient erifClient;

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {

        log.debug("AccountService.enrichAccountData, contextData={}", contextData );

        //convert context data to msgcontrol and routable objects
//        MessageControl messageControl = new MessageControl(contextData.getLocale());
        MessageControl messageControl = new MessageControl();
        messageControl.setLocale(contextData.getLocale());

//        log.debug(messageControl.toString());
        Routable routable = new Routable();
        routable.setType(RoutableType.validate.name());
        routable.setChargingId(contextData.getChargingId());
        routable.setClientId(contextData.getClientId());
        routable.setKycCheck(contextData.isKycCheck());
//        log.debug(routable.toString());

        EnrichedAccountInfo enrichedAccountInfo = erifClient.validate(messageControl, routable);

        return enrichedAccountInfo;

    }

}
