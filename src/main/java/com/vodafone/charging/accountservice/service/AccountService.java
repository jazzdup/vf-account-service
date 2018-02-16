package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

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

    @Autowired
    private AccountRepository repository;

    public AccountService() {
    }

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {
        log.debug("contextData={}", contextData);
        EnrichedAccountInfo info;
        String protocol = propertiesAccessor.getPropertyForOpco("erif.communication.protocol"
                , contextData.getLocale().getCountry(), "json");
        if ("soap".equalsIgnoreCase(protocol)) {
            log.info("doing soap");
            info = erifXmlClient.validate(contextData);
        }else{
            log.info("doing json");
            info = erifClient.validate(contextData);
        }
        repository.save(new Account(contextData.getChargingId(), info, new Date()));
        log.info("Account Data for chargingId={} saved", contextData.getChargingId().getValue());
        return info;
    }

    public Account getAccount(final ChargingId chargingId) {
        return repository.findByChargingId(chargingId);
    }

    public Account getAccount(final String accountId) {
        return repository.findOne(accountId);
    }

    public List<String> getUserGroups(final String accountId) {
        Account account = repository.findOne(accountId);
        return newArrayList(account.getProfiles().get(0).getUserGroups());
    }

}
