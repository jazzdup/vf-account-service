package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFEntry;
import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.logging.ULFLogger;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.application.util.ULFUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class LoggingUtil {

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

	protected static void handleCustomError(ULFUtils.WrappedResponse response, ULFEntry.Builder builder) {
		if (StringUtils.isNotEmpty(response.getError())) {
			final String error = ULFThreadLocal.getValue(ULFKeys.ERROR);
			if (StringUtils.isEmpty(error)) {
				builder.error(response.getError());
			}
		}
	}

	protected static void logHttpRequestIn(HttpServletRequest request, String useCaseId, String transactionId) {
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

	protected static void logHttpRequestOut(HttpRequest request, String useCaseId, String transactionId) {
		final ULFEntry.Builder builder = new ULFEntry.Builder()
				.component(UlfConstants.ULF_LOG_COMPONENT)
				.logpoint(ULFEntry.Logpoint.REQUEST_OUT.toString())
				.setValue(UlfConstants.ULF_JSESSIONID, ULFThreadLocal.getValue(UlfConstants.LOGGING_JSESSION_ID_ATTRIBUTE))
				.usecaseId(useCaseId)
				.transactionId(transactionId)
				.timestamp(new Date())

//				.inboundRequestUri(request.getRequestURI())
//				.inboundRequestIp(request.getRemoteAddr())
//				.inboundRequestPort(Integer.toString(request.getRemotePort()))
//				.serverName(request.getServerName())
				.httpMethod(request.getMethod().name())
//				.queryString(request.getQueryString())

				.opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
				.channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL));
//				.setValue(UlfConstants.ULF_USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT))
//				.setValue(UlfConstants.ULF_REFERER, request.getHeader(com.google.common.net.HttpHeaders.REFERER))
//				.setValue(UlfConstants.ULF_SOURCE, ULFThreadLocal.getValue(UlfConstants.ULF_SOURCE));
		ULFLogger.log(builder.build());
	}
	protected static void logHttpResponseIn(HttpRequest request, ClientHttpResponse response, String useCaseId, String transactionId) {
		ULFEntry.Builder builder = new ULFEntry.Builder();
		try {
			builder.setValue(UlfConstants.ULF_HTTP_STATUS_CODE, String.valueOf(response.getStatusCode()));
		} catch (IOException e) {
			log.error(e.getStackTrace().toString());
		}

		builder
				.component(UlfConstants.ULF_LOG_COMPONENT)
				.logpoint(ULFEntry.Logpoint.RESPONSE_IN.toString())
				.setValue(UlfConstants.ULF_JSESSIONID,  ULFThreadLocal.getValue(UlfConstants.LOGGING_JSESSION_ID_ATTRIBUTE))
				.usecaseId(useCaseId)
				.transactionId(transactionId)
				.timestamp(new Date())

//				.inboundRequestUri(request.getRequestURI())
//				.inboundRequestIp(request.getRemoteAddr())
//				.inboundRequestPort(Integer.toString(request.getRemotePort()))
//				.serverName(request.getServerName())
				.httpMethod(request.getMethod().name())
//				.queryString(request.getQueryString())
				.errorCode(ULFThreadLocal.getValue(ULFKeys.ERROR_CODE))
				.error(ULFThreadLocal.getValue(ULFKeys.ERROR))

				.opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
				.channel(ULFThreadLocal.getValue(ULFKeys.CHANNEL))
				.msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
				.partner(ULFThreadLocal.getValue(ULFKeys.PARTNER))

//				.redirectUrl(response.getRedirect())
//                .setValue(UlfConstants.ULF_OFFER_NAME, ULFThreadLocal.getValue(UlfConstants.ULF_OFFER_NAME))
//				.setValue(UlfConstants.ULF_USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT))
//				.setValue(UlfConstants.ULF_REFERER, request.getHeader(com.google.common.net.HttpHeaders.REFERER))
				.setValue(UlfConstants.ULF_SOURCE, ULFThreadLocal.getValue(UlfConstants.ULF_SOURCE));
//				.payload(response.getPayload());

		ULFLogger.log(builder.build());
	}
	protected static void logHttpResponseOut(HttpServletRequest request, ULFUtils.WrappedResponse response, String useCaseId, String transactionId) {
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
}
