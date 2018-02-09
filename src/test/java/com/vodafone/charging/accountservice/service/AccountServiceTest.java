package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.properties.PropertiesAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    private PropertiesAccessor propertiesAccessor;

    @Mock
    private ERIFClient erifClient;

    @Mock
    private ERIFXmlClient erifXmlClient;

    @InjectMocks
    private AccountService accountService;

    @Before
    public void init() {
        initMocks(this);
    }

    @Test
    public void shouldCallERIFClientWithoutChangingContextData() throws Exception {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        final ContextData contextData = aContextData();
        given(erifClient.validate(contextData)).willReturn(expectedInfo);
        given(propertiesAccessor.getProperty(eq("gb.erif.communication.protocol"))).willReturn("json");

        //when
        final EnrichedAccountInfo info = accountService.enrichAccountData(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);
        verify(erifClient).validate(any(ContextData.class));

    }
    @Test
    public void shouldCallERIFXmlClientWithoutChangingContextData() throws Exception {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        final ContextData contextData = aContextData();
        given(erifXmlClient.validate(contextData)).willReturn(expectedInfo);
        given(propertiesAccessor.getProperty(eq("gb.erif.communication.protocol"))).willReturn("soap");

        //when
        final EnrichedAccountInfo info = accountService.enrichAccountData(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);
        verify(erifXmlClient).validate(any(ContextData.class));
    }

    @Test
    public void shouldPropagateAnyExceptionWithoutModificationERIFClient() {
        final ContextData contextData = aContextData();
        final String message = "This is a test exception message";
        given(erifClient.validate(contextData))
                .willThrow(new RuntimeException(message));
        given(propertiesAccessor.getProperty(eq("gb.erif.communication.protocol"))).willReturn("json");

        assertThatThrownBy(()-> accountService.enrichAccountData(contextData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(message);

        verify(erifClient).validate(any(ContextData.class));
    }

    @Test
    public void shouldPropagateAnyExceptionWithoutModificationERIFXmlClient() {
        final ContextData contextData = aContextData();
        final String message = "This is a test exception message";
        given(erifXmlClient.validate(contextData))
                .willThrow(new RuntimeException(message));
        given(propertiesAccessor.getProperty(eq("gb.erif.communication.protocol"))).willReturn("soap");

        assertThatThrownBy(()-> accountService.enrichAccountData(contextData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(message);

        verify(erifXmlClient).validate(any(ContextData.class));
    }

}
