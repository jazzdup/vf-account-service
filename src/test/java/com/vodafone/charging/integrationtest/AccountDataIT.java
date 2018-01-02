package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static com.vodafone.charging.accountservice.exception.ErrorIds.VAS_INTERNAL_SERVER_ERROR;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
public class AccountDataIT {

    private MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON_UTF8.getType(),
                    MediaType.APPLICATION_JSON_UTF8.getSubtype());

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @MockBean
    private AccountService accountService;

    private String toJson(Object o) throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON_UTF8, outputMessage);
        return outputMessage.getBodyAsString();
    }

    private Object fromJson(Class<?> clazz, String json) throws IOException {
        MockHttpInputMessage input = new MockHttpInputMessage(json.getBytes());
        return mappingJackson2HttpMessageConverter.read(clazz, input);
    }

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
//        this.mockMvc = standaloneSetup(webApplicationContext).build();
    }

    @Test
    public void pathNotFound() throws Exception {

        mockMvc.perform(post("/account")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldValidateAccountAndReturnOK() throws Exception {
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        String accountJson = toJson(aContextData());

        given(accountService.enrichAccountData(any()))
                .willReturn(expectedInfo);

        MvcResult result = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final EnrichedAccountInfo info =
                (EnrichedAccountInfo) fromJson(EnrichedAccountInfo.class, result.getResponse().getContentAsString());
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);
    }

    @Test
    public void shouldThrowInternalExceptionAndReturnHttp500() throws Exception {
        final String accountJson = toJson(aContextData());

        given(accountService.enrichAccountData(any()))
                .willThrow(new RuntimeException("This is a test exception, please ignore."));

        MvcResult result = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();

        final EnrichedAccountInfo info =
                (EnrichedAccountInfo) fromJson(EnrichedAccountInfo.class, result.getResponse().getContentAsString());
        assertThat(info.getErrorId()).isEqualTo(VAS_INTERNAL_SERVER_ERROR.errorId());
        assertThat(info.getErrorDescription()).isEqualTo(VAS_INTERNAL_SERVER_ERROR.errorDescription());
    }

}
