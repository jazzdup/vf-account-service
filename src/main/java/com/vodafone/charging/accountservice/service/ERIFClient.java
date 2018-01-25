package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ERIFClient {
    private PropertiesAccessor propertiesAccessor;

    private RestTemplate restTemplate;

    public ERIFClient(RestTemplate restTemplate, PropertiesAccessor propertiesAccessor) {
        this.restTemplate = restTemplate;
        this.propertiesAccessor = propertiesAccessor;
    }

    public EnrichedAccountInfo validate(MessageControl messageControl, Routable routable) {
        //call ERIF using json
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<ERIFRequest> request = new HttpEntity<>(new ERIFRequest(messageControl, routable), httpHeaders);

        log.debug(request.toString());
        final String url = propertiesAccessor.getProperty("erif.url");

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