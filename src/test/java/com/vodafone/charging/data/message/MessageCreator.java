package com.vodafone.charging.data.message;

import com.vodafone.charging.spring.configuration.BeanConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

//TODO doesn't load the context without this
//@SpringBootTest(classes = AccountServiceApplication.class)
@Component
@ContextConfiguration(classes = BeanConfiguration.class)
public class MessageCreator {

    @Autowired
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    public MessageCreator() {
    }

    public String toJson(Object o) throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON_UTF8, outputMessage);
        return outputMessage.getBodyAsString();
    }
}
