package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.ERIFResponseData.aERIFResponse;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ERIFClientTest {

    @Mock
    private PropertiesAccessor propertiesAccessor;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ERIFClient erifClient;

    @Before
    public void init() {
        initMocks(this);
    }

    @Test
    public void shouldValidateAccountAndReturnOK() throws Exception {
        //given

        final ERIFResponse erifResponse = aERIFResponse();

        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo(erifResponse);
        final ContextData contextData = aContextData();
        final String url = "127.0.0.1:8080";

        ResponseEntity<ERIFResponse> responseEntity = new ResponseEntity<>(erifResponse, HttpStatus.OK);

        given(propertiesAccessor.getProperty("erif.url")).willReturn(url);
        given(restTemplate.postForEntity(eq(url), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
                .willReturn(responseEntity);

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }

    @Test
    public void shouldValidateAccountAndReturnOKAllFields() throws Exception {
        //given
        final ERIFResponse erifResponse = aERIFResponse();

        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo(erifResponse);

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(erifResponse, HttpStatus.OK);

        final ContextData contextData = aContextData();
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate, contextData);

        given(restTemplate.postForEntity(anyString(),
                anyObject(),
                anyObject()))
                .willReturn(responseEntity);


//        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
//                .willReturn(responseEntity);

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(messageControl, routable);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }


}
