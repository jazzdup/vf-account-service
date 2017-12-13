package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Random;

import static com.vodafone.charging.data.builder.AccountDataBuilder.aAccount;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@WebAppConfiguration
public class AccountValidationIT {

    private MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON_UTF8.getType(),
                    MediaType.APPLICATION_JSON_UTF8.getSubtype());

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private String toJson(Object o) throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON_UTF8, outputMessage);
        return outputMessage.getBodyAsString();
    }

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void pathNotFound() throws Exception {
        final String accountId = new Random().nextInt() + "";

        mockMvc.perform(post("/account/" + accountId + "/validation")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void shouldValidateAccountAndReturnOK() throws Exception {
        //given
        final String accountId = new Random().nextInt() + "";
        String accountJson = toJson(aAccount());

        //when
        mockMvc.perform(post("/accounts/" + accountId + "/validation")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
