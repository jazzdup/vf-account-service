package com.vodafone.charging.integrationtest.http;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.ERIFResponse;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import com.vodafone.charging.mock.WiremockPreparer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.ApplicationPortsEnum.DEFAULT_ER_IF_PORT;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;


//TODO This test needs to be reviewed.  Wiremock may not be the best approach.
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class, webEnvironment = DEFINED_PORT)
public class VfAccountServiceHttpTest {

    private static final Logger log = LoggerFactory.getLogger(VfAccountServiceHttpTest.class);

    private String url = "http://localhost:8080/accounts";

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private RestTemplate testRestTemplate;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(DEFAULT_ER_IF_PORT.value());

    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIFLimitedFields() throws Exception {
        //given
        final ERIFResponse erifResponse = ERIFResponse.builder()
            .status("ACCEPTED").ban("BAN_7777").errId("OK").billingCycleDay(8)
                .build();
        //set expectedInfo to be what we're setting in the mock @TODO expand to all fields
        final EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo(erifResponse);
        ChargingId chargingId = aChargingId();
        final ContextData contextData = ContextDataDataBuilder.aContextData(chargingId);
        WiremockPreparer.prepareForValidateJson(chargingId);

        //when
        ResponseEntity<EnrichedAccountInfo> responseEntity = testRestTemplate.postForEntity(url, contextData, EnrichedAccountInfo.class);
        EnrichedAccountInfo enrichedAccountInfo = responseEntity.getBody();

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);
    }

    @Test
    public void shouldAcceptJsonString() throws Exception {

        //given
        final ERIFResponse erifResponse = ERIFResponse.builder()
                .status("ACCEPTED").ban("BAN_7777").errId("OK").billingCycleDay(8)
                .build();
        //set expectedInfo to be what we're setting in the mock @TODO expand to all fields
        final EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo.Builder(erifResponse.getStatus())
                .ban(erifResponse.getBan()).errorId(erifResponse.getErrId()).billingCycleDay(erifResponse.getBillingCycleDay()).build();
        ChargingId chargingId = aChargingId();
        final ContextData contextData = ContextDataDataBuilder.aContextData(chargingId);
        WiremockPreparer.prepareForValidateJson(chargingId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(newArrayList(APPLICATION_JSON_UTF8, APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON_UTF8);

        log.info(jsonConverter.toJson(contextData.asMap()) + "\n\n");
        RequestEntity<String> requestEntity = new RequestEntity<>(jsonConverter.toJson(contextData.asMap()), headers, POST, URI.create(url));


        //when
        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(url, POST, requestEntity, Object.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //TODO Deserialize the String response and check
    }

}