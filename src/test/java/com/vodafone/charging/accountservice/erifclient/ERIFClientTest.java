package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.dto.json.ERIFRequest;
import com.vodafone.charging.accountservice.dto.json.ERIFResponse;
import com.vodafone.charging.accountservice.dto.json.MessageControl;
import com.vodafone.charging.accountservice.dto.json.Routable;
import com.vodafone.charging.accountservice.exception.NullRestResponseReceivedException;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.properties.PropertiesAccessor;
import com.vodafone.charging.validator.HttpHeaderValidator;
import com.vodafone.ppe.common.configuration.error.MissingConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.IFResponseDataBuilder.aERIFResponse;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

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

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Before
    public void init() {
        initMocks(this);
    }

    @Test
    public void shouldValidateAccountAndReturnOKWithJson() {
        //given
        final ERIFResponse erifResponse = aERIFResponse();

        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo(erifResponse);
        final ContextData contextData = aContextData();
        final String url = "http://www.vodafone.com:8080";

        ResponseEntity<ERIFResponse> responseEntity = new ResponseEntity<>(erifResponse, HttpStatus.OK);

        given(propertiesAccessor.getPropertyForOpco(eq("erif.url"), anyString())).willReturn(url);
        given(restTemplate.postForEntity(eq(url), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
                .willReturn(responseEntity);

        //when
        final ERIFResponse erifResponse2 =  erifClient.validate(contextData);
        EnrichedAccountInfo enrichedAccountInfo = new EnrichedAccountInfo(erifResponse2, null);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

        InOrder inOrder = Mockito.inOrder(propertiesAccessor, restTemplate);

        inOrder.verify(propertiesAccessor).getPropertyForOpco(anyString(), anyString());
        inOrder.verify(restTemplate).postForEntity(urlCaptor.capture(), httpEntityCaptor.capture(), Matchers.<Class<ERIFResponse>>any());
        verifyNoMoreInteractions(restTemplate, propertiesAccessor);

        final HttpEntity<ERIFRequest> request = httpEntityCaptor.getValue();
        final MessageControl messageControl = request.getBody().getMessageControl();
        final Routable routable = request.getBody().getRoutable();
        final HttpHeaders headers = request.getHeaders();

        assertThat(urlCaptor.getValue()).isEqualTo(url);
        assertThat(messageControl.getLocale()).isEqualTo(contextData.getLocale());
        assertThat(routable.getKycCheck()).isEqualTo(contextData.isKycCheck());
        assertThat(routable.getClientId()).isEqualTo(contextData.getClientId());
        assertThat(routable.getChargingId()).isEqualTo(contextData.getChargingId());
        assertThat(routable.getType()).isEqualTo(RoutableType.validate.name());

        //validate actual request and response in full:
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

        HttpHeaderValidator.validateHttpHeadersJson(headers, contextData);
    }

    @Test
    public void shouldPropagateExceptionFromPropertiesAccessor() {
        String message = "this is a test exception";
        ContextData contextData = aContextData();
        given(propertiesAccessor.getPropertyForOpco(anyString(), anyString()))
                .willThrow(new MissingConfigurationException(message));
        assertThatThrownBy(() -> erifClient.validate(contextData))
                .isInstanceOf(MissingConfigurationException.class).hasMessage(message);

    }

    @Test
    public void shouldPropagateExceptionFromRestTemplate() {
        String message = "this is a test exception";
        ContextData contextData = aContextData();
        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
                .willThrow(new RuntimeException(message));
        assertThatThrownBy(() -> erifClient.validate(contextData))
                .isInstanceOf(RuntimeException.class).hasMessage(message);
    }

    @Test
    public void shouldHandleIfNullObjectReturned() {
        String message = "Received a null response from RestClient trying to call the IF";
        ContextData contextData = aContextData();

        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), Matchers.<Class<ERIFResponse>>any()))
                .willReturn(null);

        assertThatThrownBy(() -> erifClient.validate(contextData))
                .isInstanceOf(NullRestResponseReceivedException.class)
                .hasMessage(message);
    }

}
