package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.dto.xml.*;
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


/**
 * it's not SOAP standard, uses non-legal content-type and non-default namespaces so generated
 * Jaxb files have been hacked to work
 */
@Service
@Slf4j
public class ERIFXmlClient {

    @Autowired
    private PropertiesAccessor propertiesAccessor;

    @Autowired
    private RestTemplate xmlRestTemplate;

    public ERIFXmlClient(RestTemplate xmlRestTemplate, PropertiesAccessor propertiesAccessor) {
        this.xmlRestTemplate = xmlRestTemplate;
        this.propertiesAccessor = propertiesAccessor;
    }

    public Response validate(ContextData contextData) {

        Envelope requestEnvelope = buildEnvelope(contextData);
        //generates ERIF not-quite-soap specific headers
        final ValidateHttpHeaders headers = new ValidateHttpHeaders(contextData, MediaType.TEXT_XML);

        final HttpEntity<Envelope> request = new HttpEntity<>(requestEnvelope, headers.getHttpHeaders());
        log.debug(request.toString());
        final String url = propertiesAccessor.getPropertyForOpco("erif.url", contextData.getLocale().getCountry());

        final Optional<ResponseEntity<Envelope>> responseOptional =
                Optional.ofNullable(xmlRestTemplate.postForEntity(url, request, Envelope.class));

        final ResponseEntity<Envelope> responseEntity = responseOptional
                .orElseThrow(() -> new NullRestResponseReceivedException("Received a null response from RestClient trying to call the IF"));

        final Envelope envelope = responseEntity.getBody();
        final Body body = envelope.getBody();
        final Response response = body.getResponse();

        return response;

    }

    private Envelope buildEnvelope(ContextData contextData){
        Msgcontrol msgcontrol= new Msgcontrol();
        msgcontrol.setCountry(contextData.getLocale().getCountry());

        AccountId accountId = new AccountId();
        accountId.setType(contextData.getChargingId().getType());
        accountId.setValue(contextData.getChargingId().getValue());

        Validate validate = new Validate();
        validate.setAccountId(accountId);
        validate.setClientId(contextData.getClientId());
        validate.setKycCheck(contextData.isKycCheck());
        validate.setPackageType(contextData.getPackageType().name());
        validate.setPartnerId(contextData.getPartnerId());
        validate.setServiceId(contextData.getServiceId());
        validate.setVendorId(contextData.getVendorId());

        Request request = new Request();
        request.setMsgcontrol(msgcontrol);
        request.setValidate(validate);

        Messagegroup messagegroup = new Messagegroup();
        messagegroup.setRequest(request);

        Body body = new Body();
        body.setMessagegroup(messagegroup);
        Envelope envelope = new Envelope();
        envelope.setBody(body);

        return  envelope;
    }
}