package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ERIFClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ERIFClient erifClient;

    @Before
    public void init() {
        initMocks(this);
    }

    /**
     * This test only mocks the ERIF response fields returned by the current default ERIF setup
     */
    @Test
    public void shouldValidateAccountAndReturnOK() throws Exception {
        //given
        final ERIFResponse erifResponse = ERIFResponse.builder()
                .status("ACCEPTED").ban("BAN_123").errId("OK").billingCycleDay(8)
                .build();

        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo.Builder(erifResponse.getStatus())
                .ban(erifResponse.getBan())
                .errorId(erifResponse.getErrId())
                .billingCycleDay(erifResponse.getBillingCycleDay()).build();

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(erifResponse, HttpStatus.OK);


        final ContextData contextData = ContextDataDataBuilder.aContextData();
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate, contextData);
//                new Routable(RoutableType.validate.name(), contextData.getChargingId(), contextData.getClientId(), contextData.isKycCheck());

//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        HttpEntity<ERIFRequest> request = new HttpEntity<>(new ERIFRequest(messageControl, routable), httpHeaders);

        given(restTemplate.postForEntity(anyString(),
                anyObject(),
                anyObject()))
                .willReturn(responseEntity);

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(messageControl, routable);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }


}
