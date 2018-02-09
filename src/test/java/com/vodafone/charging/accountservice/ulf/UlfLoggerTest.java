package com.vodafone.charging.accountservice.ulf;

import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, LogFactory.class, WebUtils.class})
public class UlfLoggerTest {

    @Mock
    private Logger loggerMock;
    @Mock
    private Log logMock;
    @Mock
    private PropertiesAccessor propertiesAccessor;
    @InjectMocks
    private UlfLogger ulfLogger;

    @Before
    public void before(){
        PowerMockito.mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(anyString())).thenReturn(loggerMock);
//        when(LogFactory.getLog(any(Class.class))).thenReturn(logMock);
        given(propertiesAccessor.getPropertyAsBoolean(eq("ulf.logger.without.payload.enable"), anyBoolean())).willReturn(true);
        given(propertiesAccessor.getPropertyAsBoolean(eq("ulf.logger.with.payload.enable"), anyBoolean())).willReturn(true);
        given(propertiesAccessor.isOptionalProperty(eq("ulf.logger.with.pretty.printing.enable"))).willReturn(true);
    }
    @Test
    public void shouldReturnCorrectBooleans() throws Exception {
        assertThat(ulfLogger.isEnabledLogWithoutPayload()).isTrue();
        assertThat(ulfLogger.isEnabledLogWithPayload()).isTrue();
        assertThat(ulfLogger.isEnabledLogWithPrettyPrinting()).isTrue();

//        InOrder inOrder = Mockito.inOrder(ulfLogger, ulfLogger);
//        inOrder.verify(ulfLogger).logHttpRequestIn(anyObject(), anyString(), anyString());
//        inOrder.verify(ulfLogger).logHttpResponseOut(anyObject(), anyObject(), anyString(), anyString());
//        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldLogExpectedNumberOfTimes(){
        //given
        final String UC_ID = "ucId";
        final String TX_ID = "txId";
        byte[] bytes = new byte[1];

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(httpRequest.getURI()).thenReturn(URI.create("x"));
        ClientHttpResponse httpResponse = mock(ClientHttpResponse.class);

        ContentCachingResponseWrapper wrapper = mock(ContentCachingResponseWrapper.class);
        when(wrapper.getContentAsByteArray()).thenReturn(bytes);
        when(wrapper.getCharacterEncoding()).thenReturn("UTF-8");
        PowerMockito.mockStatic(WebUtils.class);
        when(WebUtils.getNativeResponse(anyObject(), anyObject())).thenReturn(wrapper);

        HttpServletResponse response = mock(HttpServletResponse.class);

        ulfLogger.logHttpRequestIn(request, UC_ID, TX_ID);
        verify(loggerMock, times(2)).info(anyString());
        ulfLogger.logHttpRequestOut(httpRequest, bytes, UC_ID, TX_ID);
        verify(loggerMock, times(4)).info(anyString());//cumulative
        ulfLogger.logHttpResponseIn(httpRequest, httpResponse, UC_ID, TX_ID);
        verify(loggerMock, times(6)).info(anyString());//cumulative
        ulfLogger.logHttpResponseOut(request, response, UC_ID, TX_ID);
        verify(loggerMock, times(8)).info(anyString());//cumulative

    }

}