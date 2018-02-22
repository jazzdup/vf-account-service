package com.vodafone.charging.accountservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@Slf4j
public class ConsumerWrapper {
    private ConsumerWrapper() {
    }

    public static <T> Supplier<T> wrapper(Supplier<T> function) {

        return () -> {
            try {
                return function.get();
            } catch (Exception e) {
                if(e instanceof RepositoryResourceNotFoundException ||
                        e instanceof ApplicationLogicException) {
                    throw e;
                } else {
//                    E exceptionCast = clazz.cast(e);
                    throw new ApplicationLogicException(e.getMessage());
                }
            }
        };

    }
}
