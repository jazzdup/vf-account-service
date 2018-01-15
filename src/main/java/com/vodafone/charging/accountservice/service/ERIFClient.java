package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by al on 15/01/18.
 */
@Service
@Slf4j
public class ERIFClient {
    public EnrichedAccountInfo validate(MessageControl messageControl, Routable routable) {
        //call ERIF using json
        String url = "http://localhost:8458/broker/router.jsp";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ERIFRequest> request = new HttpEntity<>(new ERIFRequest(messageControl, routable), httpHeaders);

        log.debug(request.toString());
        ResponseEntity<ERIFResponse> responseEntity = restTemplate.postForEntity(url, request, ERIFResponse.class);
        ERIFResponse responseBody = responseEntity.getBody();
        log.debug(responseEntity.toString());
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