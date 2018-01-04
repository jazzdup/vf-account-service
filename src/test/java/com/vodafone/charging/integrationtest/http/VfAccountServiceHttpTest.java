package com.vodafone.charging.integrationtest.http;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.service.AccountService;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class, webEnvironment = DEFINED_PORT)
public class VfAccountServiceHttpTest {

    @MockBean
    private AccountService accountService;

    private String url = "http://localhost:8080/accounts";

    private RestTemplate restTemplate;

    @Before
    public void init() {
        restTemplate = new RestTemplate();
    }


    @Test
    public void postAccountInfo() {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        given(accountService.enrichAccountData(any())).willReturn(expectedInfo);
        ContextData contextData = ContextDataDataBuilder.aContextData("test-contextName", Locale.UK, aChargingId());

        //when
        ResponseEntity<EnrichedAccountInfo> responseEntity = restTemplate.postForEntity(url, contextData, EnrichedAccountInfo.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualToComparingFieldByField(expectedInfo);

    }

}
