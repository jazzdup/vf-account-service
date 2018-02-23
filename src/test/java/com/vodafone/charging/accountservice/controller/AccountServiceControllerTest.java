package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.exception.MethodArgumentValidationException;
import com.vodafone.charging.accountservice.exception.ResponseSupplierWrapper;
import com.vodafone.charging.accountservice.service.AccountService;
import com.vodafone.charging.accountservice.service.SpendLimitService;
import com.vodafone.charging.data.object.NullableChargingId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aNullableChargingId;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static com.vodafone.charging.data.builder.HttpHeadersDataBuilder.aHttpHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private ResponseSupplierWrapper responseSupplierWrapper;

    @Mock
    private SpendLimitService spendLimitService;

    @InjectMocks
    private AccountServiceController accountServiceController;

    @Before()
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldPassCorrectDataAndReturnOkWhenCorrectDataIsReceived() {
        ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
        //given
        final EnrichedAccountInfo expectedAccountInfo = aEnrichedAccountInfo();
        final ContextData contextData = aContextData();
        final HttpHeaders headers = aHttpHeaders(contextData.getClientId(),
                contextData.getLocale(),
                contextData.getChargingId());

        given(accountService.enrichAccountData(contextData)).willReturn(expectedAccountInfo);
        given(responseSupplierWrapper.wrap(any())).willReturn(() -> expectedAccountInfo);

        //when
        final ResponseEntity<EnrichedAccountInfo> enrichedAccountInfoResponse =
                accountServiceController.enrichAccountData(headers, contextData);

        //then
        verify(responseSupplierWrapper).wrap(captor.capture());
        final Supplier supplier = captor.getValue();
        assertThat(supplier.get()).isInstanceOf(EnrichedAccountInfo.class);

        assertThat(supplier.get()).isEqualToComparingFieldByField(expectedAccountInfo);

        assertThat(ResponseEntity.ok(expectedAccountInfo))
                .isEqualToIgnoringGivenFields(enrichedAccountInfoResponse, "body");
        assertThat(expectedAccountInfo).isEqualToComparingFieldByField(enrichedAccountInfoResponse.getBody());
    }

    @Test
    public void shouldThrowMethodArgumentValidationExceptionWhenNullChargingIdValueIsNull() {
        final ContextData contextData = aContextData("test-context-name", Locale.UK, aChargingId(null));
        assertThatThrownBy(() -> accountServiceController.checkContextData(contextData))
                .isInstanceOf(MethodArgumentValidationException.class)
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId.value is compulsory but was empty");
        verifyZeroInteractions(accountService);
    }

    @Test
    public void shouldThrowMethodArgumentValidationExceptionWhenNullChargingIdValueIsEmpty() {
        final ContextData contextData = aContextData("test-context-name", Locale.UK, aChargingId(""));
        assertThatThrownBy(() -> accountServiceController.checkContextData(contextData))
                .isInstanceOf(MethodArgumentValidationException.class)
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId.value is compulsory but was empty");
        verifyZeroInteractions(accountService);
    }

    @Test
    public void shouldThrowMethodArgumentValidationExceptionWhenNullChargingIdTypeIsNull() {

        ChargingId chargingId = aNullableChargingId(null, String.valueOf(new Random().nextInt()));

        final ContextData contextData = aContextData("test-context-name", Locale.UK, chargingId);
        assertThatThrownBy(() -> accountServiceController.checkContextData(contextData))
                .isInstanceOf(MethodArgumentValidationException.class)
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId.type is compulsory but was empty");
        verifyZeroInteractions(accountService);
    }

    @Test
    public void shouldNotWrapExceptionFromExceptionWrapper() {
        final ContextData contextData = aContextData();
        final HttpHeaders headers = aHttpHeaders(contextData.getClientId(),
                contextData.getLocale(),
                contextData.getChargingId());

        final String message = "This is a test exception";

        given(responseSupplierWrapper.wrap(any(Supplier.class))).willThrow(new NullPointerException(message));

        assertThatThrownBy(() -> accountServiceController.enrichAccountData(headers, contextData))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);

        verify(responseSupplierWrapper).wrap(any());
        verifyZeroInteractions(accountService);
    }

    @Test
    public void shouldGetAccountSuccessfullyWhenChargingIdPassed() {
        final ArgumentCaptor<ChargingId> chargingIdCaptor = ArgumentCaptor.forClass(ChargingId.class);

        final Account expectedAccount = anAccount();
        final ChargingId chargingId = expectedAccount.getChargingId();

        given(accountService.getAccount(any(ChargingId.class)))
                .willReturn(expectedAccount);

        final ResponseEntity<Account> response =
                accountServiceController.getAccount(chargingId.getType(), chargingId.getValue());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingFieldByField(expectedAccount);

        verify(accountService).getAccount(chargingIdCaptor.capture());
        verifyNoMoreInteractions(accountService);

        assertThat(chargingIdCaptor.getValue().getValue()).isEqualTo(chargingId.getValue());
        assertThat(chargingIdCaptor.getValue().getType()).isEqualTo(chargingId.getType());
    }

    @Test
    public void shouldThrowExceptionWhenIncorrectChargingIdTypePathParameter() {

        final Account expectedAccount = anAccount();
        final ChargingId chargingId = new NullableChargingId("incorrect-charging-id", "test-id");

        given(accountService.getAccount(any(ChargingId.class)))
                .willReturn(expectedAccount);

        assertThatThrownBy(() ->
                accountServiceController.getAccount(chargingId.getType(), chargingId.getValue()))
                .isInstanceOf(MethodArgumentValidationException.class)
                .hasMessageContaining("ChargingIdType");

    }

    @Test
    public void shouldWrapExceptionIntoApplicationLogicExWhenCallingGetAccount() {
        ChargingId chargingId = aChargingId();

        final String message = "This is a test exception";
        given(accountService.getAccount(any(ChargingId.class)))
                .willThrow(new NullPointerException(message));

        assertThatThrownBy(() -> accountServiceController.getAccount(chargingId.getType(), chargingId.getValue()))
                .isInstanceOf(ApplicationLogicException.class)
                .hasMessage(message)
                .hasCauseInstanceOf(NullPointerException.class);
    }

}
