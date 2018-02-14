package com.vodafone.charging.accountservice.ulf;

import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class LoggingFilterTest {

    @Mock
    private PropertiesAccessor propertiesAccessor;
    @Mock
    private UlfLogger ulfLogger;
    @InjectMocks
    private LoggingFilter loggingFilter;
    private MockHttpServletRequest request = new MockHttpServletRequest("POST",
            "/accounts");
    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void shouldLogRequestInResponseOutInOrder() throws Exception {
        MockFilterChain chain = new MockFilterChain();
        loggingFilter.doFilter(request, response, chain);
        InOrder inOrder = Mockito.inOrder(ulfLogger, ulfLogger);
        inOrder.verify(ulfLogger).logHttpRequestIn(anyObject(), anyString(), anyString(), anyString());
        inOrder.verify(ulfLogger).logHttpResponseOut(anyObject(), anyObject(), anyString(), anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldTestEmptyMethodsForStatsOnly(){
        loggingFilter.init(null);
        loggingFilter.destroy();
        Mockito.verifyZeroInteractions(ulfLogger);
    }

}