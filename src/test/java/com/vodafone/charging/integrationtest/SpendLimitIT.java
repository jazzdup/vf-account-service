package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.PaymentApproval;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.client.CatalogInfo;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import com.vodafone.charging.data.message.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.errors.ApplicationErrors.*;
import static com.vodafone.charging.data.ERTransactionDataBuilder.anErTransaction;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccountWithNullProfile;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfileWithoutSpendLimits;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimitInfoList;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimitList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@Slf4j
public class SpendLimitIT {

    private MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON_UTF8.getType(),
                    MediaType.APPLICATION_JSON_UTF8.getSubtype());

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private AccountRepository repository;

    @MockBean
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldAddSpendLimitsInProfile() throws Exception {
        Account account = anAccount(aProfileWithoutSpendLimits());
        saveAccountAndCheck(account);
        successfulUpdateSpendLimitScenario(account);
    }

    @Test
    public void shouldNotUpdateExistingSpendLimitsInAccountProfile() throws Exception {
        Account account = anAccount(aProfile());
        saveAccountAndCheck(account);
        successfulUpdateSpendLimitScenario(account);
    }

    @Test
    public void shouldReturn404WhenTryingToUpdateSpendLimitsForNonExistingAccountId() throws Exception {

        final List<SpendLimitInfo> spendLimitInfo = aSpendLimitInfoList();
        final String content = jsonConverter.toJson(spendLimitInfo);

        log.info("xml= {}", content);

        MvcResult result = mockMvc.perform(post("/accounts/NON-EXISTING-ACCOUNT-ID/profile/spendlimits")
                .content(content)
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value())).andReturn();

        AccountServiceError errorResponse =
                (AccountServiceError) jsonConverter.fromJson(AccountServiceError.class, result.getResponse().getContentAsString());
        assertThat(errorResponse.getStatus()).isEqualTo(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.status().value());
        assertThat(errorResponse.getErrorId()).isEqualTo(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.errorId().value());
        assertThat(errorResponse.getErrorDescription()).startsWith(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.errorDesciption());
    }

    @Test
    public void shouldReturn500IfProfileIsNullInDB() throws Exception {
        Account account = anAccountWithNullProfile();
        saveAccountAndCheck(account);

        final List<SpendLimitInfo> spendLimitInfo = aSpendLimitInfoList();
        final List<SpendLimit> expectedLimits = SpendLimit.fromSpendLimitsInfo(spendLimitInfo);
        final String content = jsonConverter.toJson(spendLimitInfo);

        log.info("xml= {}", content);

        MvcResult result = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/spendlimits")
                .content(content)
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value())).andReturn();

        AccountServiceError errorResponse =
                (AccountServiceError) jsonConverter.fromJson(AccountServiceError.class, result.getResponse().getContentAsString());
        assertThat(errorResponse.getStatus()).isEqualTo(APPLICATION_LOGIC_ERROR.status().value());
        assertThat(errorResponse.getErrorId()).isEqualTo(APPLICATION_LOGIC_ERROR.errorId().value());
        assertThat(errorResponse.getErrorDescription()).startsWith(APPLICATION_LOGIC_ERROR.errorDesciption());
    }

    private void saveAccountAndCheck(Account account) {
        final Account savedAccount = repository.save(account);
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount).isEqualToComparingFieldByField(account);
    }

    private void successfulUpdateSpendLimitScenario(final Account account) throws Exception {

        final List<SpendLimitInfo> spendLimitInfo = aSpendLimitInfoList();
        final List<SpendLimit> expectedLimits = SpendLimit.fromSpendLimitsInfo(spendLimitInfo);
        final String content = jsonConverter.toJson(spendLimitInfo);

        log.info("xml= {}", content);

        MvcResult result = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/spendlimits")
                .content(content)
                .contentType(contentType)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value())).andReturn();

        Account accountResult = (Account) jsonConverter.fromJson(Account.class, result.getResponse().getContentAsString());
        assertThat(accountResult.getProfiles().get(0).getSpendLimits()).isNotEmpty();
        final List<SpendLimit> resultLimits = accountResult.getProfiles().get(0).getSpendLimits();
        IntStream.range(0, resultLimits.size())
                .forEach(i -> assertThat(resultLimits.get(i)).isEqualToComparingFieldByField(expectedLimits.get(i)));
    }

    /*
    Variables - txSpendLimit
                daySpendLimit
                monthSpendLimit
                defaultTxSpendLimit -
                defaultDaySpendLimit -
                defaultMonthSpendLimit -
                billingCycleDay included - not done
                transactionsReturned or not - done
     */

    @Test
    public void shouldApprovePaymentWhenNoDefaultSuppliedAndAccountSpendLimitExistsButNotBreached() throws Exception {

        final Account account = anAccount();
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNotEmpty());

        final PaymentContext paymentContext = PaymentContext.builder()
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.00")).build()).build();

        final ERTransaction purchase = anErTransaction(new BigDecimal(2.0), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction refund = anErTransaction(new BigDecimal(2.0), LocalDateTime.now().minusSeconds(20), ERTransactionType.REFUND);

        final List<ERTransaction> transactions = newArrayList(purchase, refund);

        final ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval approval =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getResponseCode()).isEqualTo(1);
        assertThat(approval.getDescription()).isEqualTo("Approved");

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void shouldApprovePaymentWhenDefaultSuppliedAndNoAccountSpendLimitExistsButNoBreach() throws Exception {

        List<SpendLimitInfo> defaultSpendLimitInfoList = aSpendLimitInfoList(10.0, 50.0, 100.0);

        final Account account = anAccount(aProfileWithoutSpendLimits());
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNullOrEmpty());

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(defaultSpendLimitInfoList).build())
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.00")).build()).build();

        final ERTransaction purchase1 = anErTransaction(new BigDecimal(2.5), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction purchase2 = anErTransaction(new BigDecimal(4.5), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction refund = anErTransaction(new BigDecimal(3.0), LocalDateTime.now().minusSeconds(20), ERTransactionType.REFUND);

        final List<ERTransaction> transactions = newArrayList(purchase1, purchase2, refund);

        final ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval approval =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getResponseCode()).isEqualTo(1);
        assertThat(approval.getDescription()).isEqualTo("Approved");

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);

    }

    @Test
    public void shouldApprovePaymentWhenDefaultSuppliedAndAccountSpendLimitExistsAndNoBreach() throws Exception {

        final List<SpendLimitInfo> defaultSpendLimitInfoList = aSpendLimitInfoList(10.0, 50.0, 100.0);
        final List<SpendLimit> spendLimitList = aSpendLimitList(9.0, 18.0, 200.0);

        final Account account = anAccount(aProfile(spendLimitList));
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNotEmpty());

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(defaultSpendLimitInfoList).build())
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.00")).build()).build();

        final ERTransaction purchase1 = anErTransaction(new BigDecimal(2.5), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction purchase2 = anErTransaction(new BigDecimal(4.5), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction refund = anErTransaction(new BigDecimal(3.0), LocalDateTime.now().minusSeconds(20), ERTransactionType.REFUND);

        final List<ERTransaction> transactions = newArrayList(purchase1, purchase2, refund);

        final ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval approval =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getResponseCode()).isEqualTo(1);
        assertThat(approval.getDescription()).isEqualTo("Approved");

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void shouldApprovePaymentWhenEmptyTransactionListReceivedFromService() throws Exception {

        final List<SpendLimitInfo> defaultSpendLimitInfoList = aSpendLimitInfoList(10.0, 50.0, 100.0);
        final List<SpendLimit> spendLimitList = aSpendLimitList(9.0, 18.0, 200.0);

        final Account account = anAccount(aProfile(spendLimitList));
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNotEmpty());

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(defaultSpendLimitInfoList).build())
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.00")).build()).build();

        final ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(newArrayList(), HttpStatus.OK);

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval approval =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getResponseCode()).isEqualTo(1);
        assertThat(approval.getDescription()).isEqualTo("Approved");

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void shouldApprovePaymentWhenNoDefaultOrNoAccountSpendLimitExistAndThereIsATransactionHistory() throws Exception {

        final Account account = anAccount(aProfileWithoutSpendLimits());
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNullOrEmpty());

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(newArrayList()).build())
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("200000")).build()).build();

        final ERTransaction purchase1 = anErTransaction(new BigDecimal(2500.9), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction purchase2 = anErTransaction(new BigDecimal(3000.1), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction refund = anErTransaction(new BigDecimal(0.9), LocalDateTime.now().minusSeconds(20), ERTransactionType.REFUND);

        final List<ERTransaction> transactions = newArrayList(purchase1, purchase2, refund);

        final ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval approval =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getResponseCode()).isEqualTo(1);
        assertThat(approval.getDescription()).isEqualTo("Approved");

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);
    }

    public void shouldNotApprovePaymentWhenDefaultSuppliedAndAccountSpendLimitExistsAndAccountSpendLimitBreached() throws Exception {
        final List<SpendLimitInfo> defaultSpendLimitInfoList = aSpendLimitInfoList(10.0, 50.0, 100.0);
        final List<SpendLimit> spendLimitList = aSpendLimitList(9.0, 18.0, 200.0);

        final Account account = anAccount(aProfile(spendLimitList));
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNotEmpty());

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(defaultSpendLimitInfoList).build())
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("200000")).build()).build();

        final ERTransaction purchase1 = anErTransaction(new BigDecimal(2500.9), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction purchase2 = anErTransaction(new BigDecimal(3000.1), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction refund = anErTransaction(new BigDecimal(0.9), LocalDateTime.now().minusSeconds(20), ERTransactionType.REFUND);

        final List<ERTransaction> transactions = newArrayList(purchase1, purchase2, refund);

        final ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval approval =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getResponseCode()).isEqualTo(1);
        assertThat(approval.getDescription()).isEqualTo("Approved");

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void shouldApprovePaymentWhenDefaultSuppliedAndAccountSpendLimitExistsAndUnderSpendLimitButOverDefaultLimit() throws Exception {

        final List<SpendLimitInfo> defaultSpendLimitInfoList = aSpendLimitInfoList(5.0, 10.0, 20.0);
        final List<SpendLimit> spendLimitList = aSpendLimitList(20.0, 40.0, 200.0);

        final Account account = anAccount(aProfile(spendLimitList));
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNotEmpty());

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(defaultSpendLimitInfoList).build())
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.0")).build()).build();

        final ERTransaction purchase1 = anErTransaction(new BigDecimal(5.9), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction purchase2 = anErTransaction(new BigDecimal(5.1), LocalDateTime.now(), ERTransactionType.PURCHASE);
        final ERTransaction refund = anErTransaction(new BigDecimal(0.9), LocalDateTime.now().minusSeconds(20), ERTransactionType.REFUND);

        final List<ERTransaction> transactions = newArrayList(purchase1, purchase2, refund);

        final ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval approval =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getResponseCode()).isEqualTo(1);
        assertThat(approval.getDescription()).isEqualTo("Approved");

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    public void shouldReturn404ErrorWhenWhenNoAccountExists() throws Exception {

        final List<SpendLimitInfo> defaultSpendLimitInfoList = aSpendLimitInfoList(500.0, 1000.0, 2000.0);
        final ChargingId chargingId = aChargingId();

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(defaultSpendLimitInfoList).build())
                .locale(Locale.UK)
                .chargingId(chargingId)
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.0")).build()).build();

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + chargingId.getValue() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        final AccountServiceError error =
                (AccountServiceError) jsonConverter.fromJson(AccountServiceError.class, response.getResponse().getContentAsString());

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.status().value());
        assertThat(error.getErrorId()).isEqualTo(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.errorId().value());
        assertThat(error.getErrorDescription()).startsWith(REPOSITORY_RESOURCE_NOT_FOUND_ERROR.errorDesciption());

        verifyZeroInteractions(restTemplate);
    }

    @Test
    public void shouldReturn502WhenErrorReceivedFromERService() throws Exception {

        final Account account = anAccount(aProfileWithoutSpendLimits());
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNullOrEmpty());

        final List<SpendLimitInfo> defaultSpendLimitInfoList = aSpendLimitInfoList(500.0, 1000.0, 2000.0);

        final ChargingId chargingId = aChargingId();

        final PaymentContext paymentContext = PaymentContext.builder()
                .catalogInfo(CatalogInfo.builder().defaultSpendLimitInfo(defaultSpendLimitInfoList).build())
                .locale(Locale.UK)
                .chargingId(chargingId)
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.0")).build()).build();

        given(restTemplate.exchange(any(URI.class),
                eq(HttpMethod.POST),
                Matchers.<RequestEntity<ERTransactionCriteria>>any(),
                Matchers.<ParameterizedTypeReference<List<ERTransaction>>>any()))
                .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.status().isBadGateway())
                .andReturn();

        final AccountServiceError error =
                (AccountServiceError) jsonConverter.fromJson(AccountServiceError.class, response.getResponse().getContentAsString());

        assertThat(error).isNotNull();
        assertThat(error.getStatus()).isEqualTo(EXTERNAL_SERVICE_ERROR.status().value());
        assertThat(error.getErrorId()).isEqualTo(EXTERNAL_SERVICE_ERROR.errorId().value());
        assertThat(error.getErrorDescription()).startsWith(EXTERNAL_SERVICE_ERROR.errorDesciption());

        verify(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(RequestEntity.class), any(ParameterizedTypeReference.class));
        verifyNoMoreInteractions(restTemplate);

    }

}
