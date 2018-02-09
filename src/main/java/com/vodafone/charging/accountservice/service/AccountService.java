package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
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

    @Autowired
    private ERIFXmlClient erifXmlClient;

    @Autowired
    private PropertiesAccessor propertiesAccessor;

    public AccountService() {
    }

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {

        log.debug("contextData={}", contextData);
        String protocol = propertiesAccessor.getPropertyForOpco("erif.communication.protocol"
                , contextData.getLocale().getCountry(), "json");
        if ("soap".equalsIgnoreCase(protocol)){
            log.info("doing soap");
            return erifXmlClient.validate(contextData);
        }else{
            log.info("doing json");
            return erifClient.validate(contextData);
        }
    }

}
