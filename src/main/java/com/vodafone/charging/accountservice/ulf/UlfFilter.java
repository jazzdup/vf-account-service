package com.vodafone.charging.accountservice.ulf;

import com.vodafone.application.logging.ULFEntry.Builder;
import com.vodafone.application.logging.ULFEntry.Logpoint;
import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.application.util.ULFUtils;
import com.vodafone.charging.accountservice.configuration.VfCommonConstants;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class UlfFilter {

	private final UlfLogger ulfLogger;
	private final String ulfLogComponent;

	public UlfFilter(@Nonnull UlfLogger ulfLogger, @Nonnull String ulfLogComponent) {
		this.ulfLogger = ulfLogger;
		this.ulfLogComponent = ulfLogComponent;
	}

	public void logRequestOut(String outboundUrl,
			String httpMethod,
			String queryString,
			String transactionId,
			String destination,
			String serviceName,
			String serviceId,
			String payload) {

		final Builder builder = new Builder()
				.setValue(VfCommonConstants.ULF_JSESSIONID, ULFThreadLocal.getValue(VfCommonConstants.ULF_JSESSIONID))
				.msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
				.usecaseId(ULFThreadLocal.getValue(ULFKeys.USECASE_ID))
				.transactionId(transactionId)
				.timestamp(new Date())

				.component(ulfLogComponent)
				.destination(destination)
				.service(serviceName)
				.serviceId(serviceId)
				.outboundUrl(outboundUrl)
				.serverName(ULFThreadLocal.getValue(ULFKeys.SERVER_NAME))
				.logpoint(Logpoint.REQUEST_OUT.toString())
				.opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
				.httpMethod(httpMethod)
				.queryString(queryString);

		if (payload != null) {
			builder.payload(payload);
		}

		ulfLogger.log(builder.build());
	}

	public void logResponseIn(Integer httpStatus,
			String ulfStatus,
			String payload,
			String errorCode,
			String errorMessage,
			String transactionId,
			String destination,
			String serviceName,
			String serviceId) {

		final Builder builder = new Builder()
				.setValue(VfCommonConstants.ULF_JSESSIONID, ULFThreadLocal.getValue(VfCommonConstants.ULF_JSESSIONID))
				.msisdn(ULFThreadLocal.getValue(ULFKeys.MSISDN))
				.usecaseId(ULFThreadLocal.getValue(ULFKeys.USECASE_ID))
				.transactionId(transactionId)
				.timestamp(new Date())

				.component(ulfLogComponent)
				.destination(destination)
				.service(serviceName)
				.serviceId(serviceId)
				.serverName(ULFThreadLocal.getValue(ULFKeys.SERVER_NAME))
				.opco(ULFThreadLocal.getValue(ULFKeys.COUNTRY_CODE))
				.logpoint(Logpoint.RESPONSE_IN.toString())
				.status(ulfStatus);

		if (httpStatus != null) {
			builder.setValue(VfCommonConstants.ULF_HTTP_STATUS_CODE, httpStatus.toString());
			if (isErrorStatusCode(httpStatus)) {
				builder.status(ULFUtils.ERROR_STATUS);
			}
		}

		ulfLogger.logPayload(builder, payload);

		if (StringUtils.isNotEmpty(errorMessage)) {
			builder.error(errorMessage);
		}
		if (StringUtils.isNotEmpty(errorCode)) {
			builder.errorCode(errorCode);
		}

		ulfLogger.log(builder.build());
	}

	/**
	 * Check whether a HTTP status code is an error code (not 1xx or 2xx).
	 */
	private boolean isErrorStatusCode(int statusCode) {
		return statusCode >= HttpServletResponse.SC_MULTIPLE_CHOICES;
	}

}
