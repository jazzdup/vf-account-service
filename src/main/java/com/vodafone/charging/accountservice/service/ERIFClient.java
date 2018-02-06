package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ERIFClient {
    @Autowired
    private PropertiesAccessor propertiesAccessor;
    @Autowired
    private RestTemplate restTemplate;

    public ERIFClient(RestTemplate restTemplate, PropertiesAccessor propertiesAccessor) {
        this.restTemplate = restTemplate;
        this.propertiesAccessor = propertiesAccessor;
    }

    public EnrichedAccountInfo validate(ContextData contextData) {

        final MessageControl messageControl = new MessageControl(contextData.getLocale());
        final Routable routable = new Routable(RoutableType.validate, contextData);
        final ValidateHttpHeaders headers = new ValidateHttpHeaders(contextData);
        final HttpEntity<ERIFRequest> request = new HttpEntity<>(new ERIFRequest(messageControl, routable), headers.getHttpHeaders());

        log.debug(request.toString());
        final String url = propertiesAccessor.getProperty("erif.url", "http://127.0.0.1:8080");

        final ResponseEntity<ERIFResponse> responseEntity;
        responseEntity = restTemplate.postForEntity(url, request, ERIFResponse.class);

        final ERIFResponse responseBody = responseEntity.getBody();
        log.debug(responseEntity.toString());

        return new EnrichedAccountInfo(responseBody);

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

        EnrichedAccountInfo info = new EnrichedAccountInfo(responseBody);
        return info;

    }
}