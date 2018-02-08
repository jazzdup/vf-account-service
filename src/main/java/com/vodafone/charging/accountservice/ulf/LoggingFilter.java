package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.vodafone.charging.accountservice.domain.enums.ValidateHttpHeaderName.*;

/**
 * intercept requests and responses to/from ERCore/other clients.
 */
@Slf4j
@Component
public class LoggingFilter implements Filter {
    @Autowired
    private UlfLogger ulfLogger;

    public LoggingFilter(UlfLogger ulfLogger) {
        this.ulfLogger = ulfLogger;
    }

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss','SSSZ");

    protected static String getOrCreate(HttpServletRequest servletRequest, String parameter, String header, String cookie) {
        String result = servletRequest.getParameter(parameter);
        if (StringUtils.isNotEmpty(result)) {
            return result;
        }

        result = servletRequest.getHeader(header);
        if (StringUtils.isNotEmpty(result)) {
            return result;
        }

        if (StringUtils.isNotEmpty(cookie) && ArrayUtils.isNotEmpty(servletRequest.getCookies())) {
            for (Cookie c : servletRequest.getCookies()) {
                if (cookie.equals(c.getName())) {
                    result = c.getValue();
                    break;
                }
            }

            if (StringUtils.isNotEmpty(result)) {
                return result;
            }
        }

        return UUID.randomUUID().toString();
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        try {
            log.debug("in LoggingFilter");
            final HttpServletRequest request = (HttpServletRequest) servletRequest;

            final String transactionId = getOrCreate(request, UlfConstants.LOGGING_TRANSACTION_ID_ATTRIBUTE, UlfConstants.LOGGING_TRANSACTION_ID_HEADER, null);
            ULFThreadLocal.setValue(ULFKeys.TRANSACTION_ID, transactionId);

            final String useCaseId = getOrCreate(request, UlfConstants.USECASE_ID, UlfConstants.LOGGING_USECASE_ID_HEADER, UlfConstants.LOGGING_USECASE_ID_COOKIE);
            ULFThreadLocal.setValue(ULFKeys.USECASE_ID, useCaseId);

            final String jSessionId = request.getSession().getId();
            ULFThreadLocal.setValue(ULFKeys.SERVER_NAME, request.getServerName());
            ULFThreadLocal.setValue(ULFKeys.COUNTRY_CODE, request.getHeader("country"));
            ULFThreadLocal.setValue(ULFKeys.SERVICE, request.getRequestURI());
            ULFThreadLocal.setValue(ULFKeys.CHARGING_ID, request.getHeader(REQUEST_CHARGING_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.MSISDN, request.getHeader(REQUEST_MSISDN_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.CALLER_ID, request.getHeader(REQUEST_CLIENT_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.PARTNER, request.getHeader(REQUEST_PARTNER_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(UlfConstants.REQUEST_CLASS, request.getHeader(REQUEST_CLASS_HEADER_NAME.getName()));

            ULFThreadLocal.setValue(UlfConstants.REQUEST_TIMESTAMP, formatter.format(new Date()));

            HttpServletRequest requestToCache = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
            HttpServletResponse responseToCache = new ContentCachingResponseWrapper((HttpServletResponse)servletResponse);

            chain.doFilter(requestToCache, responseToCache);
            ulfLogger.logHttpRequestIn(requestToCache, useCaseId, transactionId);
            ulfLogger.logHttpResponseOut(request, responseToCache, useCaseId, transactionId);

        } finally {
            ULFThreadLocal.clean();
        }
    }

    @Override
    public void destroy() {
        //nothing to do
    }
}
