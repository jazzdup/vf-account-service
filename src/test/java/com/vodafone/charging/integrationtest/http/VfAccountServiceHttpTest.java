package com.vodafone.charging.integrationtest.http;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.data.builder.ChargingIdDataBuilder;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import com.vodafone.charging.mock.WiremockPreparer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static com.vodafone.charging.data.ApplicationPortsEnum.DEFAULT_ER_IF_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;


/**
 * Created by al on 15/01/18.
 * This is a full HTTP E2E test making http calls at the front end with restTemplate and at the back end a wiremocked ERIF
 * TODO: add test with all fields
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class, webEnvironment = DEFINED_PORT)
public class VfAccountServiceHttpTest {

//    @Autowired
//    private ERIFClient erifClient;

    private String url = "http://localhost:8080/accounts";

//    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JsonConverter converter;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(DEFAULT_ER_IF_PORT.value());

    @Before
    public void init() {
        restTemplate = new RestTemplate();
    }

    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIFLimitedFields() throws Exception {
        //given

        final ERIFResponse erifResponse = ERIFResponse.builder()
            .status("ACCEPTED").ban("BAN_7777").errId("OK").billingCycleDay(8)
                .build();
        //set expectedInfo to be what we're setting in the mock @TODO expand to all fields
        final EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo.Builder(erifResponse.getStatus())
                .ban(erifResponse.getBan()).errorId(erifResponse.getErrId()).billingCycleDay(erifResponse.getBillingCycleDay()).build();
        ChargingId chargingId = ChargingIdDataBuilder.aChargingId();
        final ContextData contextData = ContextDataDataBuilder.aContextData(chargingId);

        WiremockPreparer.prepareForValidateJson(chargingId);


        //when
        ResponseEntity<EnrichedAccountInfo> responseEntity = restTemplate.postForEntity(url, contextData, EnrichedAccountInfo.class);
        EnrichedAccountInfo enrichedAccountInfo = responseEntity.getBody();

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }
}
