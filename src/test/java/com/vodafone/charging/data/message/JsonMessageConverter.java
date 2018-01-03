package com.vodafone.charging.data.message;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

//@SpringBootTest(classes = AccountServiceApplication.class)
@Component("converter")
@ContextConfiguration(classes = AccountServiceApplication.class)
public class JsonMessageConverter {

    @Autowired
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    public JsonMessageConverter() {
    }

    public String toJson(Object o) throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON_UTF8, outputMessage);
        return outputMessage.getBodyAsString();
    }

    public Object fromJson(Class<?> clazz, String json) throws IOException {
        MockHttpInputMessage input = new MockHttpInputMessage(json.getBytes());
        return mappingJackson2HttpMessageConverter.read(clazz, input);
    }

}
