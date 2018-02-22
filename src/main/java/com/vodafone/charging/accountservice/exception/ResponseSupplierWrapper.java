package com.vodafone.charging.accountservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@Slf4j
public class ResponseSupplierWrapper {
    private ResponseSupplierWrapper() {
    }

    public <T> Supplier<T> wrap(Supplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (RepositoryResourceNotFoundException | ApplicationLogicException ex) {
                throw ex;
            } catch (Exception e) {
                throw new ApplicationLogicException(e.getMessage());
            }
        };
    }
}
