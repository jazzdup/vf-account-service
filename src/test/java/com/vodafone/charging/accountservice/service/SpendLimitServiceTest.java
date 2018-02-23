package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.Profile;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Random;

import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccountWithEmptyProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfileWithoutSpendLimits;
import static com.vodafone.charging.data.builder.SpendLimitInfoDataBuilder.aSpendLimitInfoList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SpendLimitServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private SpendLimitService spendLimitService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldUpdateAccountSpendLimits() {

        //given
        Account accountWithoutSpendLimits = anAccount(aProfileWithoutSpendLimits());
        assertThat(accountWithoutSpendLimits.getProfiles()).isNotNull();
        assertThat(accountWithoutSpendLimits.getProfiles().get(0).getSpendLimits()).isNull();

        Profile expectedProfile = aProfile();
        Account accountWithSpendLimits = anAccount(expectedProfile);
        assertThat(accountWithSpendLimits.getProfiles().get(0).getSpendLimits()).isNotNull();

        final List<SpendLimitInfo> spendLimitInfoList = aSpendLimitInfoList();
        final String accountId = String.valueOf(new Random().nextInt());

        given(accountRepository.findOne(accountId)).willReturn(accountWithoutSpendLimits);
        given(accountRepository.save(any(Account.class))).willReturn(accountWithSpendLimits);

        //when
        Account account = spendLimitService.updateSpendLimits(accountId, spendLimitInfoList);

        //then
        assertThat(account).isEqualToComparingFieldByField(accountWithSpendLimits);
        Profile profile = account.getProfiles().stream().findFirst().orElseThrow(() -> new AssertionError("no spend limits found!"));

        List<SpendLimit> limits = profile.getSpendLimits();
        assertThat(limits).isNotNull();
        assertThat(limits.size()).isEqualTo(3);

        InOrder inOrder = Mockito.inOrder(accountRepository);
        inOrder.verify(accountRepository).findOne(anyString());
        inOrder.verify(accountRepository).save(any(Account.class));
        verifyNoMoreInteractions(accountRepository);

    }

    @Test
    public void shouldThrowRepositoryExceptionIfCannotFindAccount() {
        final String accountId = String.valueOf(new Random().nextInt());

        given(accountRepository.findOne(accountId)).willReturn(null);
        given(accountRepository.save(any(Account.class))).willReturn(mock(Account.class));

        assertThatThrownBy(() -> spendLimitService.updateSpendLimits(accountId, aSpendLimitInfoList()))
                .isInstanceOf(RepositoryResourceNotFoundException.class).hasMessageContaining("No account found using id ");
    }

    @Test
    public void shouldThrowRepositoryExceptionIfEmptyProfile() {
        Account accountWithoutSpendLimits = anAccountWithEmptyProfile();
        final String accountId = String.valueOf(new Random().nextInt());

        given(accountRepository.findOne(accountId)).willReturn(accountWithoutSpendLimits);
        given(accountRepository.save(any(Account.class))).willReturn(null);

        assertThatThrownBy(() -> spendLimitService.updateSpendLimits(accountId, aSpendLimitInfoList()))
                .isInstanceOf(RepositoryResourceNotFoundException.class).hasMessageContaining("No Profile not found using account id ");
    }
}
