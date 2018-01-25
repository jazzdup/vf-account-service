package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ERIFResponse;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.data.message.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static com.vodafone.charging.accountservice.exception.ErrorIds.VAS_INTERNAL_SERVER_ERROR;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@Slf4j
public class VfAccountServiceComponentIT {

    private MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON_UTF8.getType(),
                    MediaType.APPLICATION_JSON_UTF8.getSubtype());

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JsonConverter converter;

    @MockBean
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturnNotFound404() throws Exception {

        mockMvc.perform(post("/account")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldValidateAccountAndReturnOK() throws Exception {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        String accountJson = converter.toJson(aContextData());
        ERIFResponse erifResponse = ERIFResponse.builder()
                .ban(expectedInfo.getBan())
                .billingCycleDay(expectedInfo.getBillingCycleDay())
                .errId(expectedInfo.getErrorId())
                .childServiceProviderId(expectedInfo.getChildServiceProviderId())
                .customerType(expectedInfo.getCustomerType())
                .status(expectedInfo.getValidationStatus())
                .serviceProviderId(expectedInfo.getServiceProviderId())
                .serviceProviderType(expectedInfo.getServiceProviderType())
                .usergroups(expectedInfo.getUsergroups())
                .errDescription(expectedInfo.getErrorDescription())
                .build();

        ResponseEntity<ERIFResponse> responseEntity = new ResponseEntity<>(erifResponse, HttpStatus.OK);

        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
                .willReturn(responseEntity);

        //when
        MvcResult result = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        final EnrichedAccountInfo info =
                (EnrichedAccountInfo) converter.fromJson(EnrichedAccountInfo.class, result.getResponse().getContentAsString());
        assertThat(expectedInfo).isEqualToIgnoringGivenFields(info, "customerType");
    }

    @Test
    public void shouldThrowInternalExceptionAndReturnHttp500() throws Exception {
        final String accountJson = converter.toJson(aContextData());

        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
                .willThrow(new RuntimeException("This is a test exception"));

        MvcResult result = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();

        final EnrichedAccountInfo info =
                (EnrichedAccountInfo) converter.fromJson(EnrichedAccountInfo.class, result.getResponse().getContentAsString());
        assertThat(info.getErrorId()).isEqualTo(VAS_INTERNAL_SERVER_ERROR.errorId());
        assertThat(info.getErrorDescription()).isEqualTo(VAS_INTERNAL_SERVER_ERROR.errorDescription());
    }

}
