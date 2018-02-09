package com.vodafone.charging.accountservice.ulf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vodafone.application.AppConstants;
import com.vodafone.application.logging.ULFEntry;
import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.application.util.ULFUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
@Slf4j
public class UlfLogger {
    @Autowired
    private PropertiesAccessor propertiesAccessor;

    public UlfLogger(PropertiesAccessor propertiesAccessor){
        this.propertiesAccessor = propertiesAccessor;
    }
    private Gson getGson(){
        return isEnabledLogWithPrettyPrinting() ? (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create() : (new GsonBuilder()).disableHtmlEscaping().create();
    }

    public boolean isEnabledLogWithPayload() {
        return propertiesAccessor.getPropertyAsBoolean("ulf.logger.with.payload.enable", false);
    }

    public boolean isEnabledLogWithoutPayload() {
        return propertiesAccessor.getPropertyAsBoolean("ulf.logger.without.payload.enable", true);
    }

    public boolean isEnabledLogWithPrettyPrinting() {
        return propertiesAccessor.getPropertyAsBoolean("ulf.logger.with.pretty.printing.enable", false);
    }



    public void log(ULFEntry obj) {
        if (isEnabledLogWithPayload()) {
            Logger logger = LoggerFactory.getLogger(AppConstants.ULF_WITH_PAYLOAD_LOGGER_NAME);
            logger.info(getGson().toJson(obj.getEntryElements()));
        }

        if (isEnabledLogWithoutPayload()) {
            Map<String, String> loggerElements = new TreeMap();
            loggerElements.putAll(obj.getEntryElements());
            loggerElements.remove(ULFKeys.PAYLOAD.toString());
            Logger logger = LoggerFactory.getLogger(AppConstants.ULF_WITHOUT_PAYLOAD_LOGGER_NAME);
            logger.info(getGson().toJson(loggerElements));
        }

    }

    public String toString(ULFEntry obj) {
        return getGson().toJson(obj.getEntryElements());
    }

    protected static void handleCustomError(ULFUtils.WrappedResponse response, ULFEntry.Builder builder) {
        if (StringUtils.isNotEmpty(response.getError())) {
            final String error = ULFThreadLocal.getValue(ULFKeys.ERROR);
            if (StringUtils.isEmpty(error)) {
                builder.error(response.getError());
            }
        }
    }

    protected void logHttpRequestIn(HttpServletRequest request, String useCaseId, String transactionId) {
        final ULFEntry.Builder builder = new ULFEntry.Builder()
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.REQUEST_IN.toString())
                .setValue(UlfConstants.ULF_JSESSIONID, request.getSession().getId())
                .usecaseId(useCaseId)
                .transactionId(transactionId)
                .timestamp(new Date())

                .chargingid(ULFThreadLocal.getValue(ULFKeys.CHARGING_ID))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
                .service(ULFThreadLocal.getValue((ULFKeys.SERVICE)))
                .partner(ULFThreadLocal.getValue((ULFKeys.PARTNER)))
                .callerId(ULFThreadLocal.getValue((ULFKeys.CALLER_ID)))

                .inboundRequestUri(request.getRequestURI())
                .inboundRequestIp(request.getRemoteAddr())
                .inboundRequestPort(Integer.toString(request.getRemotePort()))
                .serverName(request.getServerName())
                .httpMethod(request.getMethod())
                .queryString(request.getQueryString())

                .opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
                .channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL))
                .setValue(UlfConstants.ULF_USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT))
                .setValue(UlfConstants.ULF_REFERER, request.getHeader(com.google.common.net.HttpHeaders.REFERER))
                .setValue(UlfConstants.ULF_SOURCE, ULFThreadLocal.getValue(UlfConstants.ULF_SOURCE));
        log(builder.build());
    }

    protected void logHttpRequestOut(HttpRequest request, String useCaseId, String transactionId) {
        final ULFEntry.Builder builder = new ULFEntry.Builder()
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.REQUEST_OUT.toString())
                .usecaseId(useCaseId)
                .transactionId(transactionId)
                .timestamp(new Date())
                .chargingid(ULFThreadLocal.getValue(ULFKeys.CHARGING_ID))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
                .service(ULFThreadLocal.getValue((ULFKeys.SERVICE)))
                .partner(ULFThreadLocal.getValue((ULFKeys.PARTNER)))
                .callerId(ULFThreadLocal.getValue((ULFKeys.CALLER_ID)))
                .httpMethod(request.getMethod().name())
                .opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
                .channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL));
        log(builder.build());
    }
    protected void logHttpResponseIn(HttpRequest request, ClientHttpResponse response, String useCaseId, String transactionId) {
        ULFEntry.Builder builder = new ULFEntry.Builder();
        try {
            builder.setValue(UlfConstants.ULF_HTTP_STATUS_CODE, String.valueOf(response.getStatusCode()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        builder
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.RESPONSE_IN.toString())
                .usecaseId(useCaseId)
                .transactionId(transactionId)
                .timestamp(new Date())

                .chargingid(ULFThreadLocal.getValue(ULFKeys.CHARGING_ID))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
                .chargingid(ULFThreadLocal.getValue(ULFKeys.CHARGING_ID))
                .service(ULFThreadLocal.getValue((ULFKeys.SERVICE)))
                .partner(ULFThreadLocal.getValue((ULFKeys.PARTNER)))
                .callerId(ULFThreadLocal.getValue((ULFKeys.CALLER_ID)))
                .httpMethod(request.getMethod().name())
                .errorCode(ULFThreadLocal.getValue(ULFKeys.ERROR_CODE))
                .error(ULFThreadLocal.getValue(ULFKeys.ERROR))
                .opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
                .channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
                .partner(ULFThreadLocal.getValue(ULFKeys.PARTNER))
                .setValue(UlfConstants.ULF_SOURCE, ULFThreadLocal.getValue(UlfConstants.ULF_SOURCE));
//				.payload(response.getPayload());

        log(builder.build());
    }
    protected void logHttpResponseOut(HttpServletRequest request, ULFUtils.WrappedResponse response, String useCaseId, String transactionId) {
        final ULFEntry.Builder builder = new ULFEntry.Builder()
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.RESPONSE_OUT.toString())
                .setValue(UlfConstants.ULF_JSESSIONID, request.getSession().getId())
                .usecaseId(useCaseId)
                .transactionId(transactionId)
                .timestamp(new Date())

                .chargingid(ULFThreadLocal.getValue(ULFKeys.CHARGING_ID))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
                .chargingid(ULFThreadLocal.getValue(ULFKeys.CHARGING_ID))
                .service(ULFThreadLocal.getValue((ULFKeys.SERVICE)))
                .partner(ULFThreadLocal.getValue((ULFKeys.PARTNER)))
                .callerId(ULFThreadLocal.getValue((ULFKeys.CALLER_ID)))

                .inboundRequestUri(request.getRequestURI())
                .inboundRequestIp(request.getRemoteAddr())
                .inboundRequestPort(Integer.toString(request.getRemotePort()))
                .serverName(request.getServerName())
                .httpMethod(request.getMethod())
                .queryString(request.getQueryString())
                .setValue(UlfConstants.ULF_HTTP_STATUS_CODE, String.valueOf(response.getStatus()))
                .errorCode(ULFThreadLocal.getValue(ULFKeys.ERROR_CODE))
                .error(ULFThreadLocal.getValue(ULFKeys.ERROR))

                .opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
                .channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
                .partner(ULFThreadLocal.getValue(ULFKeys.PARTNER))

                .redirectUrl(response.getRedirect())
                .setValue(UlfConstants.ULF_USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT))
                .setValue(UlfConstants.ULF_REFERER, request.getHeader(com.google.common.net.HttpHeaders.REFERER))
                .setValue(UlfConstants.ULF_SOURCE, ULFThreadLocal.getValue(UlfConstants.ULF_SOURCE))
                .payload(response.getPayload());

        if (isErrorStatusCode(response.getStatus())) {
            builder.status(ULFUtils.ERROR_STATUS);
            handleCustomError(response, builder);

        } else {
            builder.status(ULFUtils.SUCCESS_STATUS);
        }

        log(builder.build());
    }

    /**
     * Check whether a HTTP status code is an error code (not 1xx, 2xx or 3xx)
     */
    private static boolean isErrorStatusCode(int statusCode) {
        return statusCode >= HttpServletResponse.SC_BAD_REQUEST;
    }
}
