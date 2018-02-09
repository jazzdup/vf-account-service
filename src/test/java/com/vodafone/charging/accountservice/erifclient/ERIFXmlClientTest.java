package com.vodafone.charging.accountservice.erifclient;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.domain.xml.*;
import com.vodafone.charging.accountservice.exception.NullRestResponseReceivedException;
import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
import com.vodafone.charging.accountservice.service.ERIFXmlClient;
import com.vodafone.charging.data.builder.EnvelopeData;
import com.vodafone.charging.validator.HttpHeaderValidator;
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
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ERIFXmlClientTest {

    @Mock
    private PropertiesAccessor propertiesAccessor;

    @Mock
    private RestTemplate xmlRestTemplate;

    @InjectMocks
    private ERIFXmlClient erifXmlClient;

    @Captor
    private ArgumentCaptor<HttpEntity<Envelope>> httpEntityCaptor;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Before
    public void init() {
        initMocks(this);
    }

    @Test
    public void shouldValidateAccountAndReturnOKWithJson() {
        //given
        final Envelope requestEnvelope = EnvelopeData.anEnvelope();

        //set expectedInfo to be what we're setting in the mock
        EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo(requestEnvelope.getBody().getResponse());
        final ContextData contextData = aContextData();
        final String url = "http://www.vodafone.com:8080";
        ResponseEntity<Envelope> responseEntity = new ResponseEntity<>(requestEnvelope, HttpStatus.OK);

        given(propertiesAccessor.getProperty(eq("erif.url"), anyString())).willReturn(url);
        given(xmlRestTemplate.postForEntity(eq(url), any(HttpEntity.class), Matchers.<Class<Envelope>>any()))
                .willReturn(responseEntity);

        //when
        final EnrichedAccountInfo enrichedAccountInfo = erifXmlClient.validate(contextData);

        //then
        //validate actual request and response in full:
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

        InOrder inOrder = Mockito.inOrder(propertiesAccessor, xmlRestTemplate);
        inOrder.verify(propertiesAccessor).getProperty(anyString(), anyString());
        inOrder.verify(xmlRestTemplate).postForEntity(urlCaptor.capture(), httpEntityCaptor.capture(), Matchers.<Class<Response>>any());
        verifyNoMoreInteractions(xmlRestTemplate, propertiesAccessor);

        final HttpEntity<Envelope> request = httpEntityCaptor.getValue();
        final Envelope envelope = responseEntity.getBody();
        final Body body = envelope.getBody();
        final Response response = body.getResponse();

        final Msgcontrol msgcontrol = request.getBody().getBody().getMessagegroup().getRequest().getMsgcontrol();
        final Validate validate = request.getBody().getBody().getMessagegroup().getRequest().getValidate();
        final HttpHeaders headers = request.getHeaders();

        assertThat(urlCaptor.getValue()).isEqualTo(url);

        assertThat(msgcontrol.getCountry()).isEqualTo(contextData.getLocale().getCountry());
        assertThat(validate.isKycCheck()).isEqualTo(contextData.isKycCheck());
        assertThat(validate.getClientId()).isEqualTo(contextData.getClientId());
        assertThat(validate.getAccountId().getType()).isEqualTo(contextData.getChargingId().getType());
        assertThat(validate.getPackageType()).isEqualTo(contextData.getPackageType().name());
        assertThat(validate.getPartnerId()).isEqualTo(contextData.getPartnerId());
        assertThat(validate.getServiceId()).isEqualTo(contextData.getServiceId());
        assertThat(validate.getVendorId()).isEqualTo(contextData.getVendorId());

        HttpHeaderValidator.validateHttpHeadersXml(headers, contextData);
    }

    @Test
    public void shouldPropagateExceptionFromPropertiesAccessor() {

        String message = "this is a test exception";
        ContextData contextData = aContextData();

        given(propertiesAccessor.getProperty(anyString(), anyString()))
                .willThrow(new RuntimeException(message));
        assertThatThrownBy(() -> erifXmlClient.validate(contextData))
                .isInstanceOf(Exception.class).hasMessage(message);

    }

    @Test
    public void shouldPropagateExceptionFromRestTemplate() {
        String message = "this is a test exception";
        ContextData contextData = aContextData();

        given(xmlRestTemplate.postForEntity(anyString(), any(HttpEntity.class), Matchers.<Class<Response>>any()))
                .willThrow(new RuntimeException(message));

        assertThatThrownBy(() -> erifXmlClient.validate(contextData))
                .isInstanceOf(Exception.class).hasMessage(message);
    }

    @Test
    public void shouldHandleIfNullObjectReturned() {
        String message = "Received a null response from RestClient trying to call the IF";
        ContextData contextData = aContextData();

        given(xmlRestTemplate.postForEntity(anyString(), any(HttpEntity.class), Matchers.<Class<Response>>any()))
                .willReturn(null);

        assertThatThrownBy(() -> erifXmlClient.validate(contextData))
                .isInstanceOf(NullRestResponseReceivedException.class)
                .hasMessage(message);
    }

}
