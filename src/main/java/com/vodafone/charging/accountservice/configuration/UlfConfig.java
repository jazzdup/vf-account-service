package com.vodafone.charging.accountservice.configuration;

import com.vodafone.charging.accountservice.ulf.UlfLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class UlfConfig extends WebMvcConfigurerAdapter {

    @Autowired
    UlfLogInterceptor ulfLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ulfLogInterceptor);
    }
}