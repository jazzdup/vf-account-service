package com.vodafone.charging.accountservice.configuration;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@Configuration
@ComponentScan
public class BeanConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
//        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
//        builder.indentOutput(true);
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
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

}
