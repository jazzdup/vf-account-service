package com.vodafone.charging.integrationtest.connected;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.service.AccountService;
import com.vodafone.charging.data.message.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@Slf4j
public class AccountDataConnectedIT {

    private MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON_UTF8.getType(),
                    MediaType.APPLICATION_JSON_UTF8.getSubtype());

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JsonConverter converter;

    @Autowired
    private AccountService accountService;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
//        this.mockMvc = standaloneSetup(webApplicationContext).build();
    }

    //can run this test against a running ERIF
    @Test
    public void shouldValidateAccountAndReturnOKAgainstRealERIF() throws Exception {
        log.debug("in shouldValidateAccountAndReturnOKAgainstRealERIF");
        //given
        final ContextData contextData = aContextData();
        String accountJson = converter.toJson(contextData);
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo(contextData.getChargingId());
//        given(accountService.enrichAccountData(any())).willReturn(expectedInfo);

        //when
        MvcResult result = mockMvc.perform(post("/accounts/")
                .contentType(contentType)
                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        final EnrichedAccountInfo info =
                (EnrichedAccountInfo) converter.fromJson(EnrichedAccountInfo.class, result.getResponse().getContentAsString());
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);
    }

}
