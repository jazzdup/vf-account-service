package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.function.Supplier;

/**
 * Use to call External Services using Spring
 */
@Primary
@Service
@Slf4j
public class ExternalServiceCallSupplier {

    public <T> Supplier<T> call(Supplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (HttpClientErrorException clientEx) {
                log.error("Client Exception calling ER Adapter Service HttpStatus: {}", clientEx.getStatusText());
                throw new ExternalServiceException("Client Exception calling ER Adapter Service ", clientEx);
            } catch (HttpServerErrorException serverEx) {
                log.error("HttpStatus: {}", serverEx.getStatusText());
                log.error(serverEx.getResponseBodyAsString());
                throw new ExternalServiceException("Server Exception calling ER Adapter Service ", serverEx);
            } catch (UnknownHttpStatusCodeException unknownEx) {
                log.error("Unknown Exception thrown with Status: {}", unknownEx.getStatusText());
                throw new ExternalServiceException("Unknown Http Exception ER Adapter Service", unknownEx);
            } catch (Exception e) {
                log.error("Unexpected exception from calling ER Adapter Service  Message: {}", e.getMessage());
                throw e;
            }
        };
    }

}
