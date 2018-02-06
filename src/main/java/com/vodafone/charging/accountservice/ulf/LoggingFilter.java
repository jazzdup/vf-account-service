package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.application.util.ULFUtils.WrappedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * intercept requests and responses to/from ERCore/other clients.
 */
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
        ULFThreadLocal.setValue(UlfConstants.LOGGING_JSESSION_ID_ATTRIBUTE, request.getSession().getId());

        final String usecaseId = ULFThreadLocal.getValue(ULFKeys.USECASE_ID);
        ULFThreadLocal.setValue(UlfConstants.LOGGING_USECASE_ID_ATTRIBUTE, usecaseId);

        ULFThreadLocal.setValue(ULFKeys.SERVER_NAME, request.getServerName());
//        ULFThreadLocal.setValue(ULFKeys.COUNTRY_CODE, Opco.GB);
//        ULFThreadLocal.setValue(ULFKeys.CHANNEL, ParamUtils.getParameterCaseInsensitive(request, QueryParameter.channel.toString()));
        ULFThreadLocal.setValue(ULFKeys.SERVICE, request.getRequestURI());
//        ULFThreadLocal.setValue(ULFKeys.PARTNER, PPEPartners.NOWTV.getId());

            LoggingUtil.logHttpRequestIn(request, usecaseId, transactionId);

            final WrappedResponse wrappedResponse = new WrappedResponse((HttpServletResponse) servletResponse);
            chain.doFilter(request, wrappedResponse);

            LoggingUtil.logHttpResponseOut(request, wrappedResponse, usecaseId, transactionId);

        } finally {
            ULFThreadLocal.clean();
        }
    }

    @Override
    public void destroy() {
        //nothing to do
    }
}
