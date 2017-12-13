package com.vodafone.charging.data.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
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
