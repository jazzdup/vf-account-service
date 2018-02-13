package com.vodafone.charging.accountservice.domain;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import java.util.Optional;

import static com.vodafone.application.util.Lists.newArrayList;
import static com.vodafone.charging.accountservice.domain.enums.ValidateHttpHeaderName.*;
import static org.springframework.http.MediaType.*;

/**
 * Collection to represent the headers required for ER IF
 */
public class ValidateHttpHeaders {

    private HttpHeaders httpHeaders;
    private static final String envTypeConfigPropName = "central.configuration.env.type";
    private static final String VALIDATE_REQUEST_CLASS = "VALIDATE";

    public ValidateHttpHeaders(@NonNull ContextData contextData, @NonNull MediaType... mediaType) {
        Assert.isTrue(TEXT_XML.equals(mediaType[0]) || APPLICATION_JSON.equals(mediaType[0])
                , "media type must be application/json (optional;charset=UTF-8)for JSON or text/xml for SOAP-like XML");
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(mediaType[0]);
        httpHeaders.setAccept(newArrayList(mediaType));
        httpHeaders.set(COUNTRY_HEADER_NAME.getName(), contextData.getLocale().getCountry());
        httpHeaders.set(TARGET_HEADER_NAME.getName(), contextData.getTarget().getValue());
        httpHeaders.set(REQUEST_CHARGING_ID_HEADER_NAME.getName(), contextData.getChargingId().toIfString());
        httpHeaders.set(REQUEST_MSISDN_HEADER_NAME.getName(), this.getMsisdn(contextData.getChargingId()).orElse(""));
        httpHeaders.set(REQUEST_CLIENT_ID_HEADER_NAME.getName(), contextData.getClientId());
        httpHeaders.set(REQUEST_PARTNER_ID_HEADER_NAME.getName(), contextData.getPartnerId());
        //TODO packageId is this required for Validate?
//        httpHeaders.set(ValidateHttpHeaderName.REQUEST_PACKAGE_ID_HEADER_NAME.getName(), contextData.getPackageId());
        httpHeaders.set(REQUEST_CLASS_HEADER_NAME.getName(), VALIDATE_REQUEST_CLASS);
//        httpHeaders.set(ENVIRONMENT_PROPERTY_SERVER_NAME.getName(), propertiesAccessor.getProperty(envTypeConfigPropName, ""));
    }


    /**
     * If ChargingId of type msisdn exists then return the value
     * otherwise an empty optional is returned
     */
    public Optional<String> getMsisdn(ChargingId chargingId) {
        String msisdn = null;
        if (chargingId.getType().equalsIgnoreCase(ChargingId.Type.MSISDN.type())) {
            msisdn = chargingId.getValue();
        }
        return Optional.ofNullable(msisdn);
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }
}
