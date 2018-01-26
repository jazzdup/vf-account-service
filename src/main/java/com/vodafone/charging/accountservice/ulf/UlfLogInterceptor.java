package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFEntry;
import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.logging.ULFLogger;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.application.util.ULFUtils;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.util.Date;

import static com.vodafone.charging.accountservice.ulf.LoggingUtil.handleCustomError;

/**
 * adapted from com.vodafone.eportal.filters.LoggingFilter in PPE
 * TODO: some of the fields will need tweaking also to be revisited when full set of headers is being used
 */
@Component
public class UlfLogInterceptor implements HandlerInterceptor {

    Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        log.info("Before process request");
        final String transactionId = LoggingUtil.getOrCreate(request, UlfConstants.LOGGING_TRANSACTION_ID_ATTRIBUTE, UlfConstants.LOGGING_TRANSACTION_ID_HEADER, null);
        ULFThreadLocal.setValue(ULFKeys.TRANSACTION_ID, transactionId);

        final String useCaseId = LoggingUtil.getOrCreate(request, UlfConstants.LOGGING_USECASE_ID_ATTRIBUTE, UlfConstants.LOGGING_USECASE_ID_HEADER, UlfConstants.LOGGING_USECASE_ID_COOKIE);
        ULFThreadLocal.setValue(ULFKeys.USECASE_ID, useCaseId);

        final String jSessionId = request.getSession().getId();
        MDC.put(UlfConstants.LOGGING_JSESSION_ID_ATTRIBUTE, request.getSession().getId());

        final String usecaseId = ULFThreadLocal.getValue(ULFKeys.USECASE_ID);
        MDC.put(UlfConstants.LOGGING_USECASE_ID_ATTRIBUTE, usecaseId);

        ULFThreadLocal.setValue(ULFKeys.SERVER_NAME, request.getServerName());
//        ULFThreadLocal.setValue(ULFKeys.COUNTRY_CODE, Opco.GB);
//        ULFThreadLocal.setValue(ULFKeys.CHANNEL, ParamUtils.getParameterCaseInsensitive(request, QueryParameter.channel.toString()));
        ULFThreadLocal.setValue(ULFKeys.SERVICE, request.getRequestURI());
//        ULFThreadLocal.setValue(ULFKeys.PARTNER, PPEPartners.NOWTV.getId());

        logHttpRequestIn(request, usecaseId, transactionId);

        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model){
        log.info("Method executed");
        try{
            final String transactionId = ULFThreadLocal.getValue(ULFKeys.TRANSACTION_ID);
            final String useCaseId = ULFThreadLocal.getValue(ULFKeys.USECASE_ID);
            final ULFUtils.WrappedResponse wrappedResponse = new ULFUtils.WrappedResponse((HttpServletResponse) response);
            logHttpResponseOut(request, wrappedResponse, useCaseId, transactionId);

    } finally {
        MDC.remove(UlfConstants.LOGGING_MSISDN_ATTRIBUTE);
        MDC.remove(UlfConstants.LOGGING_JSESSION_ID_ATTRIBUTE);
        MDC.remove(UlfConstants.LOGGING_USECASE_ID_ATTRIBUTE);
        MDC.remove(UlfConstants.LOGGING_TRANSACTION_ID_ATTRIBUTE);

        ULFThreadLocal.clean();
    }
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) {
        log.info("Request Completed!");
    }

    private void logHttpRequestIn(HttpServletRequest request, String useCaseId, String transactionId) {
        final ULFEntry.Builder builder = new ULFEntry.Builder()
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.REQUEST_IN.toString())
                .setValue(UlfConstants.ULF_JSESSIONID, request.getSession().getId())
                .usecaseId(useCaseId)
                .transactionId(transactionId)
                .timestamp(new Date())
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
        ULFLogger.log(builder.build());
    }

    private void logHttpResponseOut(HttpServletRequest request, ULFUtils.WrappedResponse response, String useCaseId, String transactionId) {
        final ULFEntry.Builder builder = new ULFEntry.Builder()
                .component(UlfConstants.ULF_LOG_COMPONENT)
                .logpoint(ULFEntry.Logpoint.RESPONSE_OUT.toString())
                .setValue(UlfConstants.ULF_JSESSIONID, request.getSession().getId())
                .usecaseId(useCaseId)
                .transactionId(transactionId)
                .timestamp(new Date())
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
//                .setValue(UlfConstants.ULF_OFFER_NAME, ULFThreadLocal.getValue(UlfConstants.ULF_OFFER_NAME))
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

        ULFLogger.log(builder.build());
    }


    /**
     * Check whether a HTTP status code is an error code (not 1xx or 2xx).
     */
    private boolean isErrorStatusCode(int statusCode) {
        return statusCode >= HttpServletResponse.SC_MULTIPLE_CHOICES;
    }

}