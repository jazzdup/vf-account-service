package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.exception.AccountServiceError;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import com.vodafone.charging.data.builder.MongoDataBuilder;
import com.vodafone.charging.data.builder.SpendLimitInfoDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.IntStream;

import static com.vodafone.charging.accountservice.errors.ApplicationErrors.REPOSITORY_RESOURCE_NOT_FOUND_ERROR;
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

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    AccountRepository repository;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldAddSpendLimitsInProfile() throws Exception {

        Account account = MongoDataBuilder.anAccount();

        final List<SpendLimitInfo> spendLimitInfo = SpendLimitInfoDataBuilder.aSpendLimitInfoList();
        final List<SpendLimit> expectedLimits = SpendLimit.fromSpendLimitInfo(spendLimitInfo);
        final String content = jsonConverter.toJson(spendLimitInfo);

        log.info("xml= {}", content);

        final Account savedAccount = repository.save(account);
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount).isEqualToComparingFieldByField(account);


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
    public void shouldNonUpdateExistingSpendLimitsInAccountProfile() throws Exception {
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

}
