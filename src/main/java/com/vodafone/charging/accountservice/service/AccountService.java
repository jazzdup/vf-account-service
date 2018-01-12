package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * The main service object which routes logic
 * to more specific application services.
 */
@Service
@Slf4j
public class AccountService {

    public EnrichedAccountInfo enrichAccountData(ContextData contextData) {

        log.debug("AccountService.enrichAccountData, contextData={}", contextData );

        //convert context data to msgcontrol and routable objects
        MessageControl messageControl = new MessageControl();
        messageControl.setLocale(contextData.getLocale());
//        log.debug(messageControl.toString());
        Routable routable = new Routable();
        routable.setType(RoutableType.validate.name());
        routable.setChargingId(contextData.getChargingId());
        routable.setClientId(contextData.getClientId());
        routable.setKycCheck(contextData.isKycCheck());
//        log.debug(routable.toString());

        //call ERIF using json
        String url = "http://localhost:8458/broker/router.jsp";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ERIFRequest> request = new HttpEntity<>(new ERIFRequest(messageControl, routable), httpHeaders);

        log.debug(request.toString());
        ResponseEntity<ERIFResponse> responseEntity = restTemplate.postForEntity(url, request, ERIFResponse.class);
        log.debug(responseEntity.toString());
        
        ERIFResponse responseBody = responseEntity.getBody();
        EnrichedAccountInfo.Builder builder = new EnrichedAccountInfo.Builder(responseBody.getStatus());
        builder.errorId(responseBody.getErrId())
                .errorDescription(responseBody.getErrDescription())
                .ban(responseBody.getBan())
                .billingCycleDay(responseBody.getBillingCycleDay())
                .serviceProviderId(responseBody.getServiceProviderId())
                .serviceProviderType(responseBody.getServiceProviderType())
                .childServiceProviderId(responseBody.getChildServiceProviderId())
                .usergroups(responseBody.getUsergroups());
        return builder.build();

    }
}
