package com.vodafone.charging.accountservice.client;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.properties.PropertiesAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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

    public List<ERTransaction> getTransactions(PaymentContext paymentContext, ERTransactionCriteria criteria) {

        //TODO Transaction needs to change to an ErTransactionSummary object
        final String url = propertiesAccessor.getPropertyForOpco("erif.communication.protocol",
                paymentContext.getLocale().getCountry(), "http://localhost:8094");

        final URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Incorrect application configuration for ER endpoint url.  Please check.");
        }

        final ParameterizedTypeReference<List<ERTransaction>> reference =
                new ParameterizedTypeReference<List<ERTransaction>>() {
                };

        final RequestEntity<ERTransactionCriteria> requestEntity = RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(criteria);

        ResponseEntity<List<ERTransaction>> responseEntity = null;
        try {

            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, reference);

        } catch (HttpClientErrorException clientEx) {
            log.error("Client Exception calling url {} HttpStatus: {}", url, clientEx.getStatusText());
        } catch (HttpServerErrorException serverEx) {
            log.error("HttpStatus: {}", serverEx.getStatusText());
            log.error(serverEx.getResponseBodyAsString());
            //TODO - something required with the response?
            String response = serverEx.getResponseBodyAsString();
        } catch (UnknownHttpStatusCodeException unknownEx) {
            log.error("Unknown Exception thrown with Status: {}", unknownEx.getStatusText());
        } catch (Exception e) {
            log.error("Unexpected exception from calling RestService URL: {}.  Message: {}", url, e.getMessage());
            throw e;
        }


        final HttpStatus status = responseEntity.getStatusCode();
        final List<ERTransaction> erTransactions = responseEntity.getBody();
        return erTransactions;

    }

}
