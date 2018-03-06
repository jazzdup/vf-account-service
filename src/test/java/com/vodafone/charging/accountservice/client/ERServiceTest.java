package com.vodafone.charging.accountservice.client;

import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.data.ERTransactionDataBuilder;
import com.vodafone.charging.properties.PropertiesAccessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import static com.vodafone.charging.data.builder.PaymentContextDataBuilder.aPaymentContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class ERServiceTest {

    @Mock
    private RestTemplate restTemplate;

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
        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any())).willReturn(responseEntity);

        List<ERTransaction> transactions = erService.getTransactions(paymentContext, criteria);

        assertThat(transactions).isNotEmpty();
        assertThat(transactions).containsAll(expectedTransactions);

    }


}
