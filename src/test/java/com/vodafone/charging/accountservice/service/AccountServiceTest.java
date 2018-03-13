package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import com.vodafone.charging.properties.PropertiesAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository repository;

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
        verify(repository, Mockito.times(1)).save(any(Account.class));
    }

    @Test
    public void shouldCallERIFXmlClientWithoutChangingContextData() throws Exception {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        final ContextData contextData = aContextData();
        given(erifXmlClient.validate(contextData)).willReturn(expectedInfo);
        given(propertiesAccessor.getPropertyForOpco(eq("erif.communication.protocol"), anyString(), anyString())).willReturn("soap");

        //when
        final EnrichedAccountInfo info = accountService.enrichAccountData(contextData);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);
        verify(erifXmlClient).validate(any(ContextData.class));
        verify(repository, Mockito.times(1)).save(any(Account.class));
    }

    @Test
    public void shouldPropagateAnyExceptionWithoutModificationERIFClient() {
        final ContextData contextData = aContextData();
        final String message = "This is a test exception message";
        given(erifClient.validate(contextData))
                .willThrow(new RuntimeException(message));
        given(propertiesAccessor.getProperty(eq("gb.erif.communication.protocol"))).willReturn("json");

        assertThatThrownBy(() -> accountService.enrichAccountData(contextData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(message);

        verify(erifClient).validate(any(ContextData.class));
        verifyZeroInteractions(repository);
    }

    @Test
    public void shouldPropagateAnyExceptionWithoutModificationERIFXmlClient() {
        final ContextData contextData = aContextData();
        final String message = "This is a test exception message";
        given(erifXmlClient.validate(contextData))
                .willThrow(new RuntimeException(message));
        given(propertiesAccessor.getPropertyForOpco(eq("erif.communication.protocol"), anyString(), anyString())).willReturn("soap");

        assertThatThrownBy(() -> accountService.enrichAccountData(contextData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(message);

        verify(erifXmlClient).validate(any(ContextData.class));
        verifyZeroInteractions(repository);
    }

    @Test
    public void shouldGetAccountWithAccountId() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Account account = anAccount();

        given(repository.findOne(account.getId())).willReturn(account);

        final Account accountResult = accountService.getAccount(account.getId());

        assertThat(accountResult).isEqualToComparingFieldByField(account);

        verify(repository).findOne(captor.capture());
        assertThat(captor.getValue()).isEqualToIgnoringCase(account.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void shouldGetAccountEvenIfNullId() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Account account = anAccount();

        given(repository.findOne(account.getId())).willReturn(null);

        final Account accountResult = accountService.getAccount(account.getId());

        assertThat(accountResult).isNull();

        verify(repository).findOne(captor.capture());
        assertThat(captor.getValue()).isEqualToIgnoringCase(account.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void shouldGetAccountWithChargingId() {

        final ArgumentCaptor<ChargingId> captor = ArgumentCaptor.forClass(ChargingId.class);
        final Account account = anAccount();

        given(repository.findByChargingId(account.getChargingId())).willReturn(account);

        final Account accountResult = accountService.getAccount(account.getChargingId());

        assertThat(accountResult).isEqualToComparingFieldByField(accountResult);

        verify(repository).findByChargingId(captor.capture());
        assertThat(captor.getValue()).isEqualToComparingFieldByField(account.getChargingId());
        verifyNoMoreInteractions(repository);

    }

    @Test
    public void shouldGetUserGroupWithAccountId() {

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        final Account account = anAccount();

        given(repository.findOne(account.getId())).willReturn(account);

        final List<String> userGroups = accountService.getUserGroups(account.getId());

        assertThat(userGroups).isNotEmpty();

        verify(repository).findOne(captor.capture());
        assertThat(captor.getValue()).isEqualToIgnoringCase(account.getId());
        verifyNoMoreInteractions(repository);


    }

}
