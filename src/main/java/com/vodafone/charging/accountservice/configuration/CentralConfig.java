package com.vodafone.charging.accountservice.configuration;

import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import com.vodafone.charging.accountservice.util.SimplePropertiesAccessor;
import com.vodafone.ppe.common.configuration.BasePropertiesProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = "com.vodafone.ppe.common.configuration")
@ImportResource({"classpath*:centralconfig/run-configuration-context.xml", "classpath*:centralconfig/applicationContext.xml"})
public class CentralConfig {

    @Bean(name = "propertiesAccessor")
    public PropertiesAccessor getPropertiesAccessor(BasePropertiesProvider basePropertiesProvider) {
        return new SimplePropertiesAccessor(basePropertiesProvider);
    }
}
