package com.vodafone.charging.accountservice.ulf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ERIFClientHttpRequestInterceptorTest {

    @Mock
    private PropertiesAccessor propertiesAccessor;
    @Mock
    private UlfLogger ulfLogger;
    @InjectMocks
    private ERIFClientHttpRequestInterceptor erifClientHttpRequestInterceptor;

    @Test
    public void shouldLogRequestInResponseOutInOrder() throws Exception {
        HttpRequest httpRequest = mock(HttpRequest.class);
        ClientHttpRequestExecution clientHttpRequestExecution = mock(ClientHttpRequestExecution.class);
        erifClientHttpRequestInterceptor.intercept(httpRequest, null, clientHttpRequestExecution );
        InOrder inOrder = Mockito.inOrder(ulfLogger, ulfLogger);
        inOrder.verify(ulfLogger).logHttpRequestOut(anyObject(), anyObject(), anyString(), anyString());
        inOrder.verify(ulfLogger).logHttpResponseIn(anyObject(), anyObject(), anyString(), anyString());
        inOrder.verifyNoMoreInteractions();
    }

}