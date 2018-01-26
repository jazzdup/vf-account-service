package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.builder.ERIFResponseData;
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

//TODO: may be replaced but doesn't run when moved to integrationtest dir due to @RestClientTest(ERIFClient.class) and lacking required spring config
//hence maybe we should consider this as another kind of unit test of rest client as opposed to IT.
@RunWith(SpringRunner.class)
@RestClientTest(ERIFClient.class)
public class ERIFClientRestTest {
    @Autowired
    private PropertiesAccessor propertiesAccessor;

    @Autowired
    private ERIFClient erifClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private JsonConverter converter;

    /**
     * This test only mocks the ERIF response fields returned by the current default ERIF setup
     *
     * @throws Exception
     */
    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIFWithDefaultFields() throws Exception {
        //given
        final ERIFResponse erifResponse = ERIFResponse.builder()
                .status("ACCEPTED").ban("BAN_123").errId("OK").billingCycleDay(8)
                .build();
        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo.Builder(erifResponse.getStatus())
                .ban(erifResponse.getBan()).errorId(erifResponse.getErrId()).billingCycleDay(erifResponse.getBillingCycleDay()).build();
        String url = propertiesAccessor.getProperty("erif.url");
        server.expect(requestTo(url)).andExpect(method(POST))
                .andRespond(withSuccess(converter.toJson(erifResponse), MediaType.APPLICATION_JSON));

        final ContextData contextData = ContextDataDataBuilder.aContextData();
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate.name(), contextData);

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(messageControl, routable);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }


    /**
     * This test mocks all the possible ERIF response fields according to ERIF spec on 18/1/2018
     *
     * @throws Exception
     */
    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIFWithAllFields() throws Exception {
        //given
        final ERIFResponse erifResponse = ERIFResponseData.anERIFResponse();

        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo(erifResponse);
        String url = propertiesAccessor.getProperty("erif.url");
        server.expect(requestTo(url)).andExpect(method(POST))
                .andRespond(withSuccess(converter.toJson(erifResponse), MediaType.APPLICATION_JSON));

        final ContextData contextData = ContextDataDataBuilder.aContextData();
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate.name(), contextData);

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(messageControl, routable);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }
}
