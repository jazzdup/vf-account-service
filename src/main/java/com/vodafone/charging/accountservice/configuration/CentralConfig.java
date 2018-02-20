package com.vodafone.charging.accountservice.configuration;

import com.vodafone.charging.ulf.UlfLogger;
import com.vodafone.charging.properties.PropertiesAccessor;
import com.vodafone.charging.properties.SimplePropertiesAccessor;
import com.vodafone.ppe.common.configuration.BasePropertiesProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackageClasses = {com.vodafone.ppe.common.configuration.BasePropertiesProvider.class
        , com.vodafone.charging.ulf.UlfLogger.class})
@ImportResource({"classpath*:centralconfig/run-configuration-context.xml", "classpath*:applicationContext.xml"})
public class CentralConfig {

    @Bean(name = "propertiesAccessor")
    public PropertiesAccessor getPropertiesAccessor(BasePropertiesProvider basePropertiesProvider) {
        return new SimplePropertiesAccessor(basePropertiesProvider);
    }

    @Bean(name = "ulfLogger")
    public UlfLogger ulfLogger(PropertiesAccessor propertiesAccessor){
        return new UlfLogger(propertiesAccessor);
    }
}
