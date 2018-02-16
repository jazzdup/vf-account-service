package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.dto.json.ERIFResponse;
import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.data.builder.IFResponseData;
import com.vodafone.charging.data.message.JsonConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//TODO This test is not required
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@RestClientTest(ERIFClient.class)
public class ERIFClientRestIT {
    @MockBean
    private AccountRepository repository;

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
        String url = propertiesAccessor.getPropertyForOpco("erif.url", "GB");

        server.expect(requestTo(url)).andExpect(method(POST))
                .andRespond(withSuccess(converter.toJson(erifResponse), MediaType.APPLICATION_JSON));

        final ContextData contextData = aContextData();
        //when
        final EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }

    /**
     * This test mocks all the possible ERIF response fields according to ERIF spec on 18/1/2018
     */
    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIFWithAllFields() throws Exception {
        //given
        final ERIFResponse erifResponse = IFResponseData.aERIFResponse();

        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo(erifResponse);
        String url = propertiesAccessor.getPropertyForOpco("erif.url", "GB");
        server.expect(requestTo(url)).andExpect(method(POST))
                .andRespond(withSuccess(converter.toJson(erifResponse), MediaType.APPLICATION_JSON));

        final ContextData contextData = aContextData();

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }
}
