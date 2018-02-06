package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.application.util.ULFUtils.WrappedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.vodafone.charging.accountservice.domain.enums.ValidateHttpHeaderName.*;

/**
 * intercept requests and responses to/from ERCore/other clients.
 */
@Slf4j
@Component
public class LoggingFilter implements Filter {
    @Autowired
    private UlfLogger ulfLogger;

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

            final String useCaseId = LoggingUtil.getOrCreate(request, UlfConstants.USECASE_ID, UlfConstants.LOGGING_USECASE_ID_HEADER, UlfConstants.LOGGING_USECASE_ID_COOKIE);
            ULFThreadLocal.setValue(ULFKeys.USECASE_ID, useCaseId);

            final String jSessionId = request.getSession().getId();
            ULFThreadLocal.setValue(UlfConstants.JSESSION_ID, request.getSession().getId());
            ULFThreadLocal.setValue(ULFKeys.SERVER_NAME, request.getServerName());
            ULFThreadLocal.setValue(ULFKeys.COUNTRY_CODE, request.getHeader("country"));
            ULFThreadLocal.setValue(ULFKeys.SERVICE, request.getRequestURI());
            ULFThreadLocal.setValue(ULFKeys.CHARGING_ID, request.getHeader(REQUEST_CHARGING_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.MSISDN, request.getHeader(REQUEST_MSISDN_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.CALLER_ID, request.getHeader(REQUEST_CLIENT_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(ULFKeys.PARTNER, request.getHeader(REQUEST_PARTNER_ID_HEADER_NAME.getName()));
            ULFThreadLocal.setValue(UlfConstants.REQUEST_CLASS, request.getHeader(REQUEST_CLASS_HEADER_NAME.getName()));

            ulfLogger.logHttpRequestIn(request, useCaseId, transactionId);

            final WrappedResponse wrappedResponse = new WrappedResponse((HttpServletResponse) servletResponse);
            chain.doFilter(request, wrappedResponse);

            ulfLogger.logHttpResponseOut(request, wrappedResponse, useCaseId, transactionId);

        } finally {
            ULFThreadLocal.clean();
        }
    }

    @Override
    public void destroy() {
        //nothing to do
    }
}
