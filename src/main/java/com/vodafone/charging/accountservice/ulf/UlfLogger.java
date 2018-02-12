package com.vodafone.charging.accountservice.ulf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vodafone.application.AppConstants;
import com.vodafone.application.logging.ULFEntry;
import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
public class UlfLogger {
    private static final Logger log = LoggerFactory.getLogger(UlfLogger.class);
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
//            Logger logger = LoggerFactory.getLogger(AppConstants.ULF_WITHOUT_PAYLOAD_LOGGER_NAME);
//            logger.info(getGson().toJson(obj.getEntryElements()));
        }

    }

    public String toString(ULFEntry obj) {
        return getGson().toJson(obj.getEntryElements());
    }

    protected void logHttpRequestIn(HttpServletRequest request, String useCaseId, String transactionId) {
        final ULFEntry.Builder builder = new ULFEntry.Builder()
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.REQUEST_IN.toString())
                .usecaseId(useCaseId)
                .transactionId(transactionId)
                .timestamp(ULFThreadLocal.getValue(UlfConstants.REQUEST_TIMESTAMP))

                .chargingid(ULFThreadLocal.getValue(ULFKeys.CHARGING_ID))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
//                .service(ULFThreadLocal.getValue((ULFKeys.SERVICE)))
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

        if (isEnabledLogWithPayload()) {
            try {
                ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
                if (wrapper != null) {
                    byte[] buf = wrapper.getContentAsByteArray();
                    if (buf.length > 0) {
                        String payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                        builder.payload(payload);
                    }
                }

//
//                ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest(
//                        (HttpServletRequest) request);
//                // wrappedRequest.getInputStream().read();
//                String body = IOUtils.toString(wrappedRequest.getReader());
//                log.info(wrappedRequest.getRequestURI(), wrappedRequest.getUserPrincipal(), body);
//                builder.payload(body);
//                wrappedRequest.resetInputStream();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        log(builder.build());
    }

    protected void logHttpRequestOut(HttpRequest request, byte[] bytes, String useCaseId, String transactionId) {
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
                .httpMethod(request.getMethod().toString())
                .outboundUrl(request.getURI().toString())
                .opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
                .channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL));
        if (isEnabledLogWithPayload()) {
            try {
                String payload = new String(bytes, "UTF-8");
                builder.payload(payload);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            log(builder.build());
        }
    }
    protected ClientHttpResponse logHttpResponseIn(HttpRequest request, ClientHttpResponse response, String useCaseId, String transactionId) {
        ClientHttpResponse responseCopy = new BufferingClientHttpResponseWrapper(response);

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
                .httpMethod(request.getMethod().toString())
                .errorCode(ULFThreadLocal.getValue(ULFKeys.ERROR_CODE))
                .error(ULFThreadLocal.getValue(ULFKeys.ERROR))
                .opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
                .channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL))
                .msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
                .partner(ULFThreadLocal.getValue(ULFKeys.PARTNER))
                .setValue(UlfConstants.ULF_SOURCE, ULFThreadLocal.getValue(UlfConstants.ULF_SOURCE));
        if (isEnabledLogWithPayload() ) {
            try {
                builder.payload(IOUtils.toString(responseCopy.getBody()));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        log(builder.build());
        return responseCopy;
    }

    private class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse response;

        private byte[] body;


        BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }


        public HttpStatus getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }

        public int getRawStatusCode() throws IOException {
            return this.response.getRawStatusCode();
        }

        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        public org.springframework.http.HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }

        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        public void close() {
            this.response.close();
        }

    }

    protected void logHttpResponseOut(HttpServletRequest request, HttpServletResponse response, String useCaseId, String transactionId) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        final ULFEntry.Builder builder = new ULFEntry.Builder()
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.RESPONSE_OUT.toString())
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

                .setValue(UlfConstants.ULF_USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT))
                .setValue(UlfConstants.ULF_REFERER, request.getHeader(com.google.common.net.HttpHeaders.REFERER))
                .setValue(UlfConstants.ULF_SOURCE, ULFThreadLocal.getValue(UlfConstants.ULF_SOURCE));

        try {
            if (isEnabledLogWithPayload()) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    String payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                    builder.payload(payload);
                }
            }
            wrapper.copyBodyToResponse();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log(builder.build());

    }
}
