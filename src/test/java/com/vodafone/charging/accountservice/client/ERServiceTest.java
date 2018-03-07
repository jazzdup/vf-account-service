package com.vodafone.charging.accountservice.client;

import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.accountservice.service.ExternalServiceCallSupplier;
import com.vodafone.charging.data.ERTransactionDataBuilder;
import com.vodafone.charging.properties.PropertiesAccessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import static com.vodafone.charging.data.builder.PaymentContextDataBuilder.aPaymentContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ERServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExternalServiceCallSupplier externalServiceCallSupplier;

    @Mock
    private Supplier<ResponseEntity<List<ERTransaction>>> supplier;

    @Mock
    private PropertiesAccessor propertiesAccessor;

    @InjectMocks
    private ERService erService;

    @Test
    public void shouldCallGetTransactionsSuccessfully() {

        final PaymentContext paymentContext = aPaymentContext();
        final ERTransactionCriteria criteria = ERTransactionCriteria.builder().monetaryOnly(true).locale(Locale.UK)
                .build();
        final List<ERTransaction> expectedTransactions = ERTransactionDataBuilder.anErTransactionList();
        final ResponseEntity<List<ERTransaction>> responseEntity = ResponseEntity.ok(expectedTransactions);
        final String url = "http://localhost:9999";

        given(propertiesAccessor.getPropertyForOpco(anyString(), eq(Locale.UK.getCountry()), anyString()))
                .willReturn(url);
        given(externalServiceCallSupplier.call(Matchers.<Supplier<ResponseEntity<List<ERTransaction>>>>any()))
                .willReturn(supplier);
        given(supplier.get()).willReturn(responseEntity);

        final List<ERTransaction> transactions = erService.getTransactions(paymentContext, criteria);

        assertThat(transactions).isNotEmpty();
        assertThat(transactions).containsAll(expectedTransactions);

        InOrder inOrder = Mockito.inOrder(propertiesAccessor, externalServiceCallSupplier,supplier);
        inOrder.verify(propertiesAccessor).getPropertyForOpco(anyString(), anyString(), anyString());
        inOrder.verify(externalServiceCallSupplier).call(any());
        inOrder.verify(supplier).get();

        verifyNoMoreInteractions(propertiesAccessor, externalServiceCallSupplier, supplier);

    }

    @Test
    public void shouldHandleNullResponseBody() {

        final PaymentContext paymentContext = aPaymentContext();
        final ERTransactionCriteria criteria = ERTransactionCriteria.builder().monetaryOnly(true).locale(Locale.UK)
                .build();
        final String url = "http://localhost:9999";

        ResponseEntity responseEntity = mock(ResponseEntity.class);

        given(propertiesAccessor.getPropertyForOpco(anyString(), eq(Locale.UK.getCountry()), anyString()))
                .willReturn(url);
        given(externalServiceCallSupplier.call(Matchers.<Supplier<ResponseEntity<List<ERTransaction>>>>any()))
                .willReturn(supplier);
        given(supplier.get()).willReturn(responseEntity);
        given(responseEntity.getStatusCode()).willReturn(HttpStatus.OK);
        given(responseEntity.getBody()).willReturn(null);

        final List<ERTransaction> transactions = erService.getTransactions(paymentContext, criteria);

        assertThat(transactions).isNotNull();
        assertThat(transactions).isEmpty();

        InOrder inOrder = Mockito.inOrder(propertiesAccessor, externalServiceCallSupplier,supplier, responseEntity);
        inOrder.verify(propertiesAccessor).getPropertyForOpco(anyString(), anyString(), anyString());
        inOrder.verify(externalServiceCallSupplier).call(any());
        inOrder.verify(supplier).get();
        inOrder.verify(responseEntity).getStatusCode();
        inOrder.verify(responseEntity).getBody();

        verifyNoMoreInteractions(propertiesAccessor, externalServiceCallSupplier, supplier, responseEntity);
    }


    @Test
    public void shouldPropagateExceptionServiceCallSupplier() {

        final PaymentContext paymentContext = aPaymentContext();
        final ERTransactionCriteria criteria = ERTransactionCriteria.builder().monetaryOnly(true).locale(Locale.UK)
                .build();
        final String url = "http://localhost:9999";
        final String exceptionMessage = "this is a test message " + new Random().nextDouble();

        given(propertiesAccessor.getPropertyForOpco(anyString(), eq(Locale.UK.getCountry()), anyString()))
                .willReturn(url);
        given(externalServiceCallSupplier.call(Matchers.<Supplier<ResponseEntity<List<ERTransaction>>>>any()))
                .willThrow(new NullPointerException(exceptionMessage));

        assertThatThrownBy(() -> erService.getTransactions(paymentContext, criteria)).isInstanceOf(NullPointerException.class)
        .hasMessage(exceptionMessage);
    }

    @Test
    public void shouldPropagateExceptionFromSupplierExecution() {
        final PaymentContext paymentContext = aPaymentContext();
        final ERTransactionCriteria criteria = ERTransactionCriteria.builder().monetaryOnly(true).locale(Locale.UK)
                .build();

        final String url = "http://localhost:9999";
        String exceptionMessage = "this is a test message " + new Random().nextDouble();

        given(propertiesAccessor.getPropertyForOpco(anyString(), eq(Locale.UK.getCountry()), anyString()))
                .willReturn(url);
        given(externalServiceCallSupplier.call(Matchers.<Supplier<ResponseEntity<List<ERTransaction>>>>any()))
                .willReturn(supplier);
        given(supplier.get()).willThrow(new NullPointerException(exceptionMessage));

        assertThatThrownBy(() -> erService.getTransactions(paymentContext, criteria)).isInstanceOf(RuntimeException.class)
                .hasMessage(exceptionMessage);

        InOrder inOrder = Mockito.inOrder(propertiesAccessor, externalServiceCallSupplier,supplier);
        inOrder.verify(propertiesAccessor).getPropertyForOpco(anyString(), anyString(), anyString());
        inOrder.verify(externalServiceCallSupplier).call(any());
        inOrder.verify(supplier).get();
    }
}
