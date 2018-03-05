package com.vodafone.charging.integrationtest;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.PaymentApproval;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import com.vodafone.charging.data.ERTransactionDataBuilder;
import com.vodafone.charging.data.builder.SpendLimitDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.vodafone.charging.accountservice.errors.ApplicationErrors.APPLICATION_LOGIC_ERROR;
import static com.vodafone.charging.accountservice.errors.ApplicationErrors.REPOSITORY_RESOURCE_NOT_FOUND_ERROR;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccountWithNullProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfileWithoutSpendLimits;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    public void shouldNonUpdateExistingSpendLimitsInAccountProfile() throws Exception {
        Account account = anAccount(aProfile());
        saveAccountAndCheck(account);
        successfulUpdateSpendLimitScenario(account);
    }

    @Test
    public void shouldReturn404WhenTryingToUpdateSpendLimitsForNonExistingAccountId() throws Exception {

        final List<SpendLimitInfo> spendLimitInfo = SpendLimitDataBuilder.aSpendLimitInfoList();
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

        final List<SpendLimitInfo> spendLimitInfo = SpendLimitDataBuilder.aSpendLimitInfoList();
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

        final List<SpendLimitInfo> spendLimitInfo = SpendLimitDataBuilder.aSpendLimitInfoList();
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


            /* --- FEATURE ---
        - get account using accountId
            if not found then return validationFailed
        - see if account has a spend limit associated with it
                - if not then see if request has a default spend limit
                        - if not the return a validationFailed
                        - if yes then get the transactions for the account for the last month
                          Calculate whether spend limit breached
                          - if not then return validationSuccess
                          - if breached return validationFailed
        - if yes then get the transactions for the account for the last month.
            Calculate whether the limit is breached for any of the limits set.
            - if not then return validationSuccess
                - if breached return validationFailed
         */

    @Test
    public void shouldValidatePaymentWhenNoDefaultSuppliedAndAccountSpendLimitExists() throws Exception {

        final Account account = anAccount();
        saveAccountAndCheck(account);
        account.getProfiles().stream()
                .findFirst().ifPresent(value -> assertThat(value.getSpendLimits()).isNotEmpty());

        PaymentContext paymentContext = PaymentContext.builder()
//                .catalogInfo(CatalogInfo.builder().build())
                .locale(Locale.UK)
                .chargingId(account.getChargingId())
                .transactionInfo(TransactionInfo.builder().amount(new BigDecimal("2.00")).build()).build();

        //TODO ER Should respond with a bunch of standard Transactions which we can test with diffent limits set.

        ERTransaction purchase = ERTransactionDataBuilder.anErTransaction(new BigDecimal(2.0), LocalDateTime.now(), ERTransactionType.PURCHASE);
        ERTransaction refund = ERTransactionDataBuilder.anErTransaction(new BigDecimal(2.0), LocalDateTime.now().minusSeconds(20), ERTransactionType.REFUND);

        List<ERTransaction> transactions = Lists.newArrayList(purchase, refund);

//        ResponseEntity<ERTransaction> responseEntity = ResponseEntity.ok(transactions);
        ResponseEntity<List<ERTransaction>> responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);

        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
                .willReturn(responseEntity);

        final String json = jsonConverter.toJson(paymentContext);

        final MvcResult response = mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentApproval validation =
                (PaymentApproval) jsonConverter.fromJson(PaymentApproval.class, response.getResponse().getContentAsString());

        assertThat(validation).isNotNull();
        assertThat(validation.isSuccess()).isTrue();
        assertThat(validation.getResponseCode()).isEqualTo(1);
        assertThat(validation.getDescription()).isEqualTo("Approved");
    }

    public void shouldValidatePaymentWhenDefaultSuppliedAndNoAccountSpendLimitExists() {
    }

    public void shouldValidatePaymentWhenDefaultSuppliedAndAccountSpendLimitExists() {
    }

    public void shouldValidatePaymentUsingAccountSpendLimitWhenDefaultSuppliedAndAccountSpendLimitExists() {
    }

    public void shouldNotValidatePaymentWhenNoAccountExists() {
    }

    public void shouldNotValidatePaymentWhenNoDefaultOrNoAccountSpendLimitExists() {
    }

    public void shouldNotValidatePaymentWhenErrorReceivedFromTransactionService() {
    }

    public void shouldValidatePaymentWhenEmptyTransactionListReceivedFromService() {
    }

    public void shouldReturnHttpErrorWhenNoBodyPassedInRequest() {
    }

    public void shouldReturnHttpErrorWhenNoAccountIdPassedInRequest() {
    }

    public void shouldNotAllowTransactionAmountToMoreThan2DecimalPlaces() {

    }


}
