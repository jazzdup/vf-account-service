package com.vodafone.charging.accountservice;

import com.vodafone.charging.accountservice.service.ERDateCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TestBeanConfiguration {

    @Bean
    public TimeZone timeZone() {
        return TimeZone.getTimeZone("CET");
    }

    @Bean
    public ERDateCalculator erDateCalculator() {
        return new ERDateCalculator();
    }

}
