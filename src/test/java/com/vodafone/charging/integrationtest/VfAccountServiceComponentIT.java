package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ChargingId.Type;
import com.vodafone.charging.accountservice.domain.ERIFResponse;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.errors.ERCoreErrorId;
import com.vodafone.charging.accountservice.errors.ERCoreErrorStatus;
import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.data.message.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;
import java.util.Random;

import static com.vodafone.charging.accountservice.errors.ApplicationErrors.*;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.*;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aNullableContextData;
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
    public void shouldReturnNotFound404() throws Exception {

        mockMvc.perform(post("/account")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
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

        final AccountServiceError error =
                (AccountServiceError) converter.fromJson(AccountServiceError.class, result.getResponse().getContentAsString());
        assertThat(error.getStatus()).isEqualTo(APPLICATION_LOGIC_ERROR.status().value());
        assertThat(error.getErrorId()).isEqualTo(APPLICATION_LOGIC_ERROR.errorId().value());
        assertThat(error.getErrorDescription()).isEqualTo(APPLICATION_LOGIC_ERROR.errorDesciption());
    }


    @Test
    public void shouldReturnHttp400WhenContextDataIsNull() throws Exception {

        MvcResult response = mockMvc.perform(post("/accounts/")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final AccountServiceError error =
                (AccountServiceError) converter.fromJson(AccountServiceError.class, response.getResponse().getContentAsString());
        assertThat(error.getStatus()).isEqualTo(MESSAGE_NOT_READABLE_ERROR.status().value());
        assertThat(error.getErrorId()).isEqualTo(MESSAGE_NOT_READABLE_ERROR.errorId().value());
        assertThat(error.getErrorDescription()).isEqualTo(MESSAGE_NOT_READABLE_ERROR.errorDesciption());
    }

    @Test
    public void shouldReturnHttp400WhenLocaleIsNotProvided() throws Exception {
        final String accountJson = converter.toJson(aNullableContextData("context-name", null, aChargingId()));

        MvcResult response = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final AccountServiceError error =
                (AccountServiceError) converter.fromJson(AccountServiceError.class, response.getResponse().getContentAsString());
        assertThat(error.getStatus()).isEqualTo(BAD_REQUEST_ERROR.status().value());
        assertThat(error.getErrorId()).isEqualTo(BAD_REQUEST_ERROR.errorId().value());
        assertThat(error.getErrorDescription()).contains("Field error in object 'contextData' on field 'locale'");
        assertThat(error.getErrorDescription()).contains("'locale' is compulsory and cannot be null");
    }

    @Test
    public void shouldReturnHttp400WhenChargingIdIsNull() throws Exception {
        final String accountJson = converter.toJson(aNullableContextData("context-name", Locale.UK, null));

        MvcResult response = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final AccountServiceError error =
                (AccountServiceError) converter.fromJson(AccountServiceError.class, response.getResponse().getContentAsString());
        assertThat(error.getStatus()).isEqualTo(BAD_REQUEST_ERROR.status().value());
        assertThat(error.getErrorId()).isEqualTo(BAD_REQUEST_ERROR.errorId().value());
        assertThat(error.getErrorDescription()).contains("Field error in object 'contextData' on field 'chargingId'");
        assertThat(error.getErrorDescription()).contains("'chargingId' is compulsory and cannot be null");
    }

    @Test
    public void shouldReturnHttp400WhenChargingIdMsisdnIsNull() throws Exception {

        final ChargingId chargingId = aNullableChargingId(Type.MSISDN.type(), null);
        final String accountJson = converter.toJson(aNullableContextData("context-name",
                Locale.UK,
                chargingId));

        MvcResult response = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final AccountServiceError error =
                (AccountServiceError) converter.fromJson(AccountServiceError.class, response.getResponse().getContentAsString());
        assertThat(error.getStatus()).isEqualTo(ERCoreErrorStatus.ERROR.value());
        assertThat(error.getErrorId()).isEqualTo(ERCoreErrorId.SYSTEM_ERROR.value());
        assertThat(error.getErrorDescription()).isEqualTo("chargingId.value is compulsory but was empty");
    }

    @Test
    public void shouldReturnHttp400WhenChargingIdTypeIsNull() throws Exception {

        final ChargingId chargingId = aNullableChargingId(null, String.valueOf(new Random().nextInt()));
        final String accountJson = converter.toJson(aNullableContextData("context-name",
                Locale.UK,
                chargingId));

        MvcResult response = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final AccountServiceError error =
                (AccountServiceError) converter.fromJson(AccountServiceError.class, response.getResponse().getContentAsString());
        assertThat(error.getStatus()).isEqualTo(ERCoreErrorStatus.ERROR.value());
        assertThat(error.getErrorId()).isEqualTo(ERCoreErrorId.SYSTEM_ERROR.value());
        assertThat(error.getErrorDescription()).isEqualTo("chargingId.type is compulsory but was empty");
    }

    @Test
    public void shouldPassHeadersInRequest() throws Exception {

        ChargingId chargingId = aChargingId();
        HttpHeaders headers = new HttpHeaders();
        headers.add("request.msisdn.header.name", chargingId.getValue());

        final String accountJson = converter.toJson(aContextData("context-name", Locale.UK, chargingId));

        MvcResult response = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson)
                .headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(response.getResponse().getStatus()).isEqualTo(HttpStatus.OK);


    }
}
