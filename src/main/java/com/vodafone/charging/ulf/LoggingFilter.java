package com.vodafone.charging.ulf;

import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private static String getOrCreate(HttpServletRequest servletRequest, String parameter, String header, String cookie) {
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
            ULFThreadLocal.setValue(ULFKeys.COUNTRY_CODE, request.getHeader(COUNTRY_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.SERVICE, request.getRequestURI());
            ULFThreadLocal.setValue(ULFKeys.CHARGING_ID, request.getHeader(REQUEST_CHARGING_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.MSISDN, request.getHeader(REQUEST_MSISDN_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.CALLER_ID, request.getHeader(REQUEST_CLIENT_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.PARTNER, request.getHeader(REQUEST_PARTNER_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(UlfConstants.REQUEST_CLASS, request.getHeader(REQUEST_CLASS_HEADER_NAME.getName()));

            ULFThreadLocal.setValue(UlfConstants.REQUEST_TIMESTAMP, formatter.format(new Date()));


            ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest(request);
            String payload = IOUtils.toString(wrappedRequest.getReader());
            wrappedRequest.resetInputStream();

            HttpServletResponse responseToCache = new ContentCachingResponseWrapper((HttpServletResponse)servletResponse);
            ulfLogger.logHttpRequestIn(wrappedRequest, payload, useCaseId, transactionId);

            chain.doFilter(wrappedRequest, responseToCache);
            ulfLogger.logHttpResponseOut(request, responseToCache, useCaseId, transactionId);

        } finally {
            ULFThreadLocal.clean();
        }
    }

    @Override
    public void destroy() {
        //nothing to do
    }

    private static class ResettableStreamHttpServletRequest extends
            HttpServletRequestWrapper {

        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }


        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }


        private class ResettableServletInputStream extends ServletInputStream {

            private ByteArrayInputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        }
    }
}
