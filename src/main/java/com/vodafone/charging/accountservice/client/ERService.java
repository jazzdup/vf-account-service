package com.vodafone.charging.accountservice.client;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.accountservice.exception.ApplicationConfigurationException;
import com.vodafone.charging.accountservice.service.ExternalServiceCallSupplier;
import com.vodafone.charging.properties.PropertiesAccessor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import static java.util.Optional.ofNullable;

/**
 * Where all interaction with ER Core or it's adapter application takes place
 */
@Service
@Slf4j
public class ERService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PropertiesAccessor propertiesAccessor;

    @Autowired
    private ExternalServiceCallSupplier externalServiceCallSupplier;

    public List<ERTransaction> getTransactions(@NonNull final PaymentContext paymentContext,
                                               @NonNull final ERTransactionCriteria criteria) {

        final URI uri = getUri(paymentContext.getLocale());
        final ParameterizedTypeReference<List<ERTransaction>> reference =
                new ParameterizedTypeReference<List<ERTransaction>>() {
                };

        final RequestEntity<ERTransactionCriteria> requestEntity = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(criteria);

        ResponseEntity<List<ERTransaction>> responseEntity;

        responseEntity = externalServiceCallSupplier.call(() ->
                restTemplate.exchange(uri, HttpMethod.POST, requestEntity, reference))
                .get();

        final HttpStatus status = responseEntity.getStatusCode();
        log.error("HttpStatus: {}", status.value());

        return ofNullable(responseEntity.getBody()).orElse(Lists.newArrayList());
    }

    public URI getUri(final Locale locale) {

        final String url = propertiesAccessor.getPropertyForOpco("er.adapter.endpoint.url",
                locale.getCountry(), "http://localhost:8094");

        final URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new ApplicationConfigurationException("Incorrect application configuration for property " +
                    "er.adapter.endpoint.url. Please check.", e);
        }
        return uri;
    }

}
