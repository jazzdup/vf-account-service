package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.dto.json.ERIFRequest;
import com.vodafone.charging.accountservice.dto.json.ERIFResponse;
import com.vodafone.charging.accountservice.dto.json.MessageControl;
import com.vodafone.charging.accountservice.dto.json.Routable;
import com.vodafone.charging.accountservice.exception.NullRestResponseReceivedException;
import com.vodafone.charging.properties.PropertiesAccessor;
import com.vodafone.charging.ulf.ValidateHttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

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

    public ERIFResponse validate(ContextData contextData) {

        final MessageControl messageControl = new MessageControl(contextData.getLocale());
        final Routable routable = new Routable(RoutableType.validate, contextData);
        final ValidateHttpHeaders headers = new ValidateHttpHeaders(contextData, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8);
        final HttpEntity<ERIFRequest> request = new HttpEntity<>(new ERIFRequest(messageControl, routable), headers.getHttpHeaders());

        log.debug(request.toString());
        final String url = propertiesAccessor.getPropertyForOpco("erif.url", contextData.getLocale().getCountry());

        final Optional<ResponseEntity<ERIFResponse>> responseOptional =
                Optional.ofNullable(restTemplate.postForEntity(url, request, ERIFResponse.class));

        final ResponseEntity<ERIFResponse> responseEntity = responseOptional
                .orElseThrow(() -> new NullRestResponseReceivedException("Received a null response from RestClient trying to call the IF"));

        final ERIFResponse responseBody = responseEntity.getBody();
        log.debug(responseEntity.toString());

        return responseBody;

    }
}