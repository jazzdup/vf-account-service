package com.vodafone.charging.accountservice.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.vodafone.charging.accountservice.util.SimplePropertiesAccessor;
import com.vodafone.ppe.common.configuration.BasePropertiesProvider;
import com.vodafone.ppe.common.configuration.CentralConfigurationService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Spring Bean configuration file
 */
@Configuration
public class BeanConfiguration extends WebMvcConfigurerAdapter {


    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
//        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.indentOutput(true);
        builder.dateFormat(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
//                .registerModule(new Jackson2HalModule())
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        //required for date formatting to follow annotations on class in Java 8
        objectMapper.findAndRegisterModules();

        //required to see private members for serialisation (e.g. in 3rd party apps)
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(messageConverters);
    }


    @Bean(name = "propertiesAccessor")
    public SimplePropertiesAccessor getPropertiesAccessor(BasePropertiesProvider basePropertiesProvider) {
        return new SimplePropertiesAccessor(basePropertiesProvider);
    }
//    @Bean(name=baseP)
//    public SimplePropertiesAccessor getPropertiesAccessor(CentralConfigurationService centralConfigurationService) {
//        return new CentralConfigurationService()
//    }


}
