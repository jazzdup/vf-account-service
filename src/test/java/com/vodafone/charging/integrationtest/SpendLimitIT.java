package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.PaymentValidation;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import com.vodafone.charging.data.builder.SpendLimitInfoDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.IntStream;

import static com.vodafone.charging.accountservice.errors.ApplicationErrors.APPLICATION_LOGIC_ERROR;
import static com.vodafone.charging.accountservice.errors.ApplicationErrors.REPOSITORY_RESOURCE_NOT_FOUND_ERROR;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccountWithNullProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfileWithoutSpendLimits;
import static org.assertj.core.api.Assertions.assertThat;
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

        final List<SpendLimitInfo> spendLimitInfo = SpendLimitInfoDataBuilder.aSpendLimitInfoList();
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

        final List<SpendLimitInfo> spendLimitInfo = SpendLimitInfoDataBuilder.aSpendLimitInfoList();
        final List<SpendLimit> expectedLimits = SpendLimit.fromSpendLimitInfo(spendLimitInfo);
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

        final List<SpendLimitInfo> spendLimitInfo = SpendLimitInfoDataBuilder.aSpendLimitInfoList();
        final List<SpendLimit> expectedLimits = SpendLimit.fromSpendLimitInfo(spendLimitInfo);
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


    @Test
    public void shouldValidatePaymentWhenNoDefaultSuppliedAndAccountSpendLimitExists() throws Exception {

        /*
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

//        final Account expectedAccount = anAccount();
//        final ChargingId expectedChargingId = expectedAccount.getChargingId();
//        Account savedAccount = repository.save(expectedAccount);
//        assertThat(expectedAccount).isEqualToComparingFieldByField(savedAccount);

        final Account account = anAccount();
        saveAccountAndCheck(account);

        final String json = jsonConverter.toJson(account);

        final MvcResult response =  mockMvc.perform(post("/accounts/" + account.getId() + "/profile/transactions/payments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final PaymentValidation validation =
                (PaymentValidation) jsonConverter.fromJson(PaymentValidation.class, response.getResponse().getContentAsString());

        assertThat(validation).isNotNull();
        assertThat(validation.isSuccess()).isTrue();

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


}
