package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFEntry;
import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.logging.ULFLogger;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.application.util.ULFUtils;
import com.vodafone.application.util.ULFUtils.WrappedResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Date;

import static com.vodafone.charging.accountservice.ulf.LoggingUtil.handleCustomError;
@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        try {
        log.info("Doing Filter");
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

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

            final WrappedResponse wrappedResponse = new WrappedResponse((HttpServletResponse) servletResponse);
            chain.doFilter(request, wrappedResponse);

            logHttpResponseOut(request, wrappedResponse, usecaseId, transactionId);

        } finally {
        MDC.remove(UlfConstants.LOGGING_MSISDN_ATTRIBUTE);
        MDC.remove(UlfConstants.LOGGING_JSESSION_ID_ATTRIBUTE);
        MDC.remove(UlfConstants.LOGGING_USECASE_ID_ATTRIBUTE);
        MDC.remove(UlfConstants.LOGGING_TRANSACTION_ID_ATTRIBUTE);

            ULFThreadLocal.clean();
        }
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
     * Check whether a HTTP status code is an error code (not 1xx, 2xx or 3xx)
     */
    private static boolean isErrorStatusCode(int statusCode) {
        return statusCode >= HttpServletResponse.SC_BAD_REQUEST;
    }

    @Override
    public void destroy() {
        //nothing to do
    }
}
