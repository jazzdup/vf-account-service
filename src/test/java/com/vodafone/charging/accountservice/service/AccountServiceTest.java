package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
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
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    private ERIFClient erifClient;

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

        //when
        final EnrichedAccountInfo info = accountService.enrichAccountData(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);
        verify(erifClient).validate(any(ContextData.class));

    }

    @Test
    public void shouldPropogateAnyExceptionWithoutModification() {
        final ContextData contextData = aContextData();
        final String message = "This is a test exception message";
        given(erifClient.validate(contextData))
                .willThrow(new RuntimeException(message));

        assertThatThrownBy(()-> accountService.enrichAccountData(contextData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(message);

        verify(erifClient).validate(any(ContextData.class));
    }

}
