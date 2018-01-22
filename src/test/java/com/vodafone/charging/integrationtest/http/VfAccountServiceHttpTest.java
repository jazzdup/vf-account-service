package com.vodafone.charging.integrationtest.http;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.service.AccountService;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class, webEnvironment = DEFINED_PORT)
public class VfAccountServiceHttpTest {

    private static final Logger log = LoggerFactory.getLogger(VfAccountServiceHttpTest.class);

    @MockBean
    private AccountService accountService;

    private String url = "http://localhost:8080/accounts";

    private RestTemplate restTemplate;

    @Before
    public void init() {
        restTemplate = new RestTemplate();
    }

    @Autowired
    private JsonConverter jsonConverter;

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

    @Test
    public void shouldAcceptJsonString() throws Exception {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        given(accountService.enrichAccountData(any())).willReturn(expectedInfo);
        ContextData contextData = ContextDataDataBuilder.aContextData("test-contextName", Locale.UK, aChargingId());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(newArrayList(APPLICATION_JSON_UTF8, APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON_UTF8);

        log.info(jsonConverter.toJson(contextData.asMap()) + "\n\n");
        RequestEntity<String> requestEntity = new RequestEntity<>(jsonConverter.toJson(contextData.asMap()), headers, POST, URI.create(url));

        //when
        ResponseEntity<Object> responseEntity = restTemplate.exchange(url, POST, requestEntity, Object.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //TODO Deserialize the String response and check
    }

}
