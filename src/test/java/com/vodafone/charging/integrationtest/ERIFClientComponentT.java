package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * Created by al on 15/01/18.
 * TODO: dont' use springmockserver for unit test, change to use wiremock for ERIF
 * TODO: once that works, convert to a full HTTP E2E test against wiremocked ERIF by moving to VfAccountServiceHttpTest
 */
@RunWith(SpringRunner.class)
@RestClientTest(ERIFClient.class)
public class ERIFClientComponentT {

    @Autowired
    private ERIFClient erifClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private JsonConverter converter;

    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIF() throws Exception {
        //given
        final ERIFResponse erifResponse = ERIFResponse.builder()
            .status("ACCEPTED").ban("BAN_123").errId("OK").billingCycleDay(8)
                .build();
        //set expectedInfo to be what we're setting in the mock @TODO expand to all fields
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo.Builder(erifResponse.getStatus())
                .ban(erifResponse.getBan()).errorId(erifResponse.getErrId()).billingCycleDay(erifResponse.getBillingCycleDay()).build();

        server.expect(requestTo(ERIFClient.url)).andExpect(method(POST))
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
