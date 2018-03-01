package com.vodafone.charging.accountservice.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.vodafone.charging.ulf.ERIFClientHttpRequestInterceptor;
import com.vodafone.charging.ulf.LoggingFilter;
import com.vodafone.charging.ulf.UlfLogger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Filter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Spring Bean configuration file
 */
@Configuration
//@EnableSwagger2
public class BeanConfiguration extends WebMvcConfigurerAdapter {

    private static final int DECIMAL_SCALE = 2;

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

    /**
     * configures ERIFClientHttpRequestInterceptor for logging request/responses to/from ERIF
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, UlfLogger ulfLogger) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.setInterceptors(Collections.singletonList(new ERIFClientHttpRequestInterceptor(ulfLogger)));
        return restTemplate;
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(messageConverters);
    }

    @Bean
    public BigDecimal transactionAmount() {
        BigDecimal decimal = BigDecimal.ZERO;
        return decimal.setScale(DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    @Bean
    public FilterRegistrationBean loggingFilterRegistration(UlfLogger ulfLogger){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(loggingFilter(ulfLogger));
        registration.addUrlPatterns("/accounts");
        registration.setName("loggingFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public TimeZone timeZone() {
        return TimeZone.getTimeZone("CET");
    }

    public Filter loggingFilter(UlfLogger ulfLogger){
        return new LoggingFilter(ulfLogger);
    }

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }


}
