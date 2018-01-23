package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.builder.ERIFResponseData;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ERIFClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ERIFClient erifClient;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * This test only mocks the ERIF response fields returned by the current default ERIF setup
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldValidateAccountAndReturnOK() throws Exception {
        //given
        final ERIFResponse erifResponse = ERIFResponseData.anERIFResponse();
        final ResponseEntity<ERIFResponse> responseEntity = new ResponseEntity<>(erifResponse, HttpStatus.OK);
        final EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo(erifResponse);
        given(restTemplate.postForEntity(anyString(), any(), eq(ERIFResponse.class)))
                .willReturn(responseEntity);
        final ContextData contextData = ContextDataDataBuilder.aContextData();
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate.name(), contextData);

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(messageControl, routable);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);
    }
}
