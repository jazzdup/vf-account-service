package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.exception.ExternalServiceException;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Primary
@Component
@Slf4j
public class ServiceCallSupplier {

    public <T> Supplier<T> call(Supplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (RepositoryResourceNotFoundException | ApplicationLogicException | ExternalServiceException ex) {
                throw ex;
            } catch (Exception e) {
                throw new ApplicationLogicException(e.getMessage(), e);
            }
        };
    }
}
