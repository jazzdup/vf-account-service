package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * Created by al on 15/01/18.
 */
@RunWith(SpringRunner.class)
@RestClientTest(ERIFClient.class)
public class ERIFClientTest {

    @Autowired
    private ERIFClient erifClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private JsonConverter converter;

    private RestTemplate restTemplate;
    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIF() throws Exception {
        //given
//        final ERIFResponse erifResponse = new ERIFResponse("ACCEPTED", "BAN_123", "OK", 8);
        final ERIFResponse erifResponse = ERIFResponse.builder()
            .status("ACCEPTED").ban("BAN_123").errId("OK").billingCycleDay(8)
                .build();
        //set expectedInfo to be what we're setting in the mock @TODO expand to all fields
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo.Builder(erifResponse.getStatus())
                .ban(erifResponse.getBan()).errorId(erifResponse.getErrId()).billingCycleDay(erifResponse.getBillingCycleDay()).build();

        this.server.expect(requestTo(ERIFClient.url))
                .andRespond(withSuccess(converter.toJson(erifResponse), MediaType.APPLICATION_JSON));

        final ContextData contextData = ContextDataDataBuilder.aContextData();
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate.name(), contextData.getChargingId(), contextData.getClientId(), contextData.isKycCheck());

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(messageControl, routable);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }
}
