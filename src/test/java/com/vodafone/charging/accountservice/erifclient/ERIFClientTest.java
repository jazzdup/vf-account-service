package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import com.vodafone.charging.validator.HttpHeaderValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.ERIFResponseData.aERIFResponse;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.*;

@RunWith(MockitoJUnitRunner.class)
public class ERIFClientTest {

    @Mock
    private PropertiesAccessor propertiesAccessor;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ERIFClient erifClient;

    @Captor
    private ArgumentCaptor<HttpEntity<ERIFRequest>> httpEntityCaptor;

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
        final String url = "http://www.vodafone.com:8080";

        ResponseEntity<ERIFResponse> responseEntity = new ResponseEntity<>(erifResponse, HttpStatus.OK);

        given(propertiesAccessor.getProperty(eq("erif.url"), anyString())).willReturn(url);
        given(restTemplate.postForEntity(eq(url), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
                .willReturn(responseEntity);

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

        verify(propertiesAccessor).getProperty(anyString(), anyString());
        verify(restTemplate).postForEntity(anyString(), httpEntityCaptor.capture(), Matchers.<Class<ERIFResponse>>any());
        verifyNoMoreInteractions(restTemplate, propertiesAccessor);

        final HttpEntity<ERIFRequest> request = httpEntityCaptor.getValue();
        final MessageControl messageControl = request.getBody().getMessageControl();
        final Routable routable = request.getBody().getRoutable();
        final HttpHeaders headers = request.getHeaders();

        assertThat(messageControl.getLocale()).isEqualTo(contextData.getLocale());
        assertThat(routable.getKycCheck()).isEqualTo(contextData.isKycCheck());
        assertThat(routable.getClientId()).isEqualTo(contextData.getClientId());
        assertThat(routable.getChargingId()).isEqualTo(contextData.getChargingId());
        assertThat(routable.getType()).isEqualTo(RoutableType.validate.name());

        assertThat(headers.getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(headers.getAccept()).contains(APPLICATION_JSON, APPLICATION_JSON_UTF8);

        assertThat(headers.getContentType()).isEqualTo(APPLICATION_JSON);

        HttpHeaderValidator.validateHttpHeaders(headers, contextData);

    }

}
