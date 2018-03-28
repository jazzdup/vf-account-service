package com.vodafone.charging.ulf;

import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * intercept requests and responses to/from ERIF
 */
@Slf4j
public class ERIFClientHttpRequestInterceptor implements ClientHttpRequestInterceptor{
    @Autowired
    private UlfLogger ulfLogger;

    public ERIFClientHttpRequestInterceptor(UlfLogger ulfLogger){
        this.ulfLogger = ulfLogger;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        log.debug("logging intercept before");
        final String transactionId = ULFThreadLocal.getValue(ULFKeys.TRANSACTION_ID);
        final String useCaseId = ULFThreadLocal.getValue(ULFKeys.USECASE_ID);

        ulfLogger.logHttpRequestOut(httpRequest, bytes, useCaseId, transactionId);

        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        log.debug("logging intercept after");

        response = ulfLogger.logHttpResponseIn(httpRequest, response, useCaseId, transactionId);

        return response;
    }
}
