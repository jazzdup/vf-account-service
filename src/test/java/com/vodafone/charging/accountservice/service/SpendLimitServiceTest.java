package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.client.ERService;
import com.vodafone.charging.accountservice.domain.ApprovalCriteria;
import com.vodafone.charging.accountservice.domain.PaymentApproval;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.PaymentApprovalRule;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.Profile;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import com.vodafone.charging.data.ERTransactionDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.dto.er.ERTransactionType.*;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccountWithEmptyProfile;
import static com.vodafone.charging.data.builder.PaymentContextDataBuilder.aPaymentContext;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfile;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfileWithoutSpendLimits;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimitInfoList;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aStandardSpendLimitList;
import static com.vodafone.charging.data.builder.SpendLimitResultDataBuilder.aSpendLimitResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpendLimitServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ERService erService;

    @Mock
    private SpendLimitChecker spendLimitChecker;

    @Mock
    private ERDateCalculator erDateCalculator;

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

    @Test
    public void shouldCalculateFromDateCorrectly() {

        Account account = anAccount();
        final LocalDateTime fromDate = spendLimitService.calculateTransactionFromDate(account);
        System.out.println("Date: " + fromDate);
    }

    @Test
    public void shouldReturnSuccessWhenAllSpendLimitsProvidedAndNoneAreBreached() {

        final Account account = anAccount();
        final List<SpendLimit> spendLimits = aStandardSpendLimitList();
        final List<SpendLimit> defaultSpendLimits = aStandardSpendLimitList();
        final PaymentContext paymentContext = aPaymentContext();

        final String reasonMessage = "Approved";
        final SpendLimitResult txLimitResult = aSpendLimitResult(true, reasonMessage, SpendLimitType.ACCOUNT_TX);
        final SpendLimitResult dayLimitResult = aSpendLimitResult(true, reasonMessage, SpendLimitType.ACCOUNT_DAY);
        final SpendLimitResult monthLimitResult = aSpendLimitResult(true, reasonMessage, SpendLimitType.ACCOUNT_MONTH);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class))).willReturn(newArrayList());
        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willReturn(txLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_DAY), anyInt()))
                .willReturn(dayLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_MONTH), anyInt()))
                .willReturn(monthLimitResult);

        final PaymentApproval approval =
                spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext);

        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getDescription()).contains(reasonMessage);

        InOrder inOrder = inOrder(erService, spendLimitChecker);

        inOrder.verify(erService).getTransactions(any(PaymentContext.class), any(ERTransactionCriteria.class));
        inOrder.verify(spendLimitChecker).checkTransactionLimit(anyListOf(SpendLimit.class),
                anyListOf(SpendLimit.class),
                anyListOf(TransactionInfo.class),
                any(SpendLimitType.class));
        inOrder.verify(spendLimitChecker).checkDurationLimit(any(PaymentContext.class), anyListOf(SpendLimit.class),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_DAY),
                anyInt());
        inOrder.verify(spendLimitChecker).checkDurationLimit(any(PaymentContext.class), anyListOf(SpendLimit.class),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_MONTH),
                anyInt());
        verifyNoMoreInteractions(erService, spendLimitChecker);
    }

    @Test
    public void shouldReturnFailureWhenTransactionSpendLimitBreached() {

        Account account = anAccount();
        List<SpendLimit> spendLimits = aStandardSpendLimitList();
        List<SpendLimit> defaultSpendLimits = aStandardSpendLimitList();
        PaymentContext paymentContext = aPaymentContext();

        String reasonMessage = "This is a test reason" + this.getClass().hashCode();
        final SpendLimitResult txLimitResult = aSpendLimitResult(false, reasonMessage, SpendLimitType.ACCOUNT_TX);


        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class))).willReturn(newArrayList());
        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willReturn(txLimitResult);

        final PaymentApproval approval =
                spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext);

        assertThat(approval.isSuccess()).isFalse();
        assertThat(approval.getDescription()).contains(reasonMessage);

        verify(erService).getTransactions(any(PaymentContext.class), any(ERTransactionCriteria.class));
        verify(spendLimitChecker).checkTransactionLimit(anyListOf(SpendLimit.class),
                anyListOf(SpendLimit.class),
                anyListOf(TransactionInfo.class),
                any(SpendLimitType.class));
        verifyNoMoreInteractions(spendLimitChecker);
    }

    @Test
    public void shouldReturnFailureWhenDaysSpendLimitBreached() {

        Account account = anAccount();
        List<SpendLimit> spendLimits = aStandardSpendLimitList();
        List<SpendLimit> defaultSpendLimits = aStandardSpendLimitList();
        PaymentContext paymentContext = aPaymentContext();

        String reasonMessage = "This is a test reason" + this.getClass().hashCode();
        final SpendLimitResult txLimitResult = aSpendLimitResult(true, reasonMessage, SpendLimitType.ACCOUNT_TX);
        final SpendLimitResult dayLimitResult = aSpendLimitResult(false, reasonMessage, SpendLimitType.ACCOUNT_DAY);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class))).willReturn(newArrayList());
        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willReturn(txLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_DAY), anyInt()))
                .willReturn(dayLimitResult);

        final PaymentApproval approval =
                spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext);

        assertThat(approval.isSuccess()).isFalse();
        assertThat(approval.getDescription()).contains(reasonMessage);

        InOrder inOrder = inOrder(erService, spendLimitChecker);

        inOrder.verify(erService).getTransactions(any(PaymentContext.class), any(ERTransactionCriteria.class));
        inOrder.verify(spendLimitChecker).checkTransactionLimit(anyListOf(SpendLimit.class),
                anyListOf(SpendLimit.class),
                anyListOf(TransactionInfo.class),
                any(SpendLimitType.class));
        inOrder.verify(spendLimitChecker).checkDurationLimit(any(PaymentContext.class), anyListOf(SpendLimit.class),
                anyListOf(ERTransaction.class),
                any(SpendLimitType.class),
                anyInt());
        verifyNoMoreInteractions(spendLimitChecker);
    }

    @Test
    public void shouldReturnFailureWhenMonthSpendLimitBreached() {

        final Account account = anAccount();
        final List<SpendLimit> spendLimits = aStandardSpendLimitList();
        final List<SpendLimit> defaultSpendLimits = aStandardSpendLimitList();
        final PaymentContext paymentContext = aPaymentContext();

        final String reasonMessage = "This is a test reason" + this.getClass().hashCode();
        final SpendLimitResult txLimitResult = aSpendLimitResult(true, reasonMessage, SpendLimitType.ACCOUNT_TX);
        final SpendLimitResult dayLimitResult = aSpendLimitResult(true, reasonMessage, SpendLimitType.ACCOUNT_DAY);
        final SpendLimitResult monthLimitResult = aSpendLimitResult(false, reasonMessage, SpendLimitType.ACCOUNT_MONTH);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class))).willReturn(newArrayList());
        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willReturn(txLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_DAY), anyInt()))
                .willReturn(dayLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_MONTH), anyInt()))
                .willReturn(monthLimitResult);

        final PaymentApproval approval =
                spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext);

        assertThat(approval.isSuccess()).isFalse();
        assertThat(approval.getDescription()).contains(reasonMessage);

        InOrder inOrder = inOrder(erService, spendLimitChecker);

        inOrder.verify(erService).getTransactions(any(PaymentContext.class), any(ERTransactionCriteria.class));
        inOrder.verify(spendLimitChecker).checkTransactionLimit(anyListOf(SpendLimit.class),
                anyListOf(SpendLimit.class),
                anyListOf(TransactionInfo.class),
                any(SpendLimitType.class));
        inOrder.verify(spendLimitChecker).checkDurationLimit(any(PaymentContext.class),
                anyListOf(SpendLimit.class),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_DAY),
                anyInt());
        inOrder.verify(spendLimitChecker).checkDurationLimit(any(PaymentContext.class),
                anyListOf(SpendLimit.class),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_MONTH),
                anyInt());
        verifyNoMoreInteractions(erService, spendLimitChecker);

    }


    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNullArgs() {
        final Account account = anAccount();
        final List<SpendLimit> spendLimits = aStandardSpendLimitList();
        final List<SpendLimit> defaultSpendLimits = aStandardSpendLimitList();
        final PaymentContext paymentContext = aPaymentContext();

        assertThatThrownBy(() -> spendLimitService.checkSpendLimits(null, spendLimits, defaultSpendLimits, paymentContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("account is null");
        assertThatThrownBy(() -> spendLimitService.checkSpendLimits(account, null, defaultSpendLimits, paymentContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("spendLimits is null");
        assertThatThrownBy(() -> spendLimitService.checkSpendLimits(account, spendLimits, null, paymentContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("defaultSpendLimits is null");
        assertThatThrownBy(() -> spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("paymentContext is null");
    }

    @Test
    public void shouldPropogateExceptionThrownBySpendLimitService() {
        final Account account = anAccount();
        final List<SpendLimit> spendLimits = aStandardSpendLimitList();
        final List<SpendLimit> defaultSpendLimits = aStandardSpendLimitList();
        final PaymentContext paymentContext = aPaymentContext();

        String message = "This is a test exception " + new Random().nextInt();
        final SpendLimitResult txLimitResult = aSpendLimitResult(true, message, SpendLimitType.ACCOUNT_TX);
        final SpendLimitResult dayLimitResult = aSpendLimitResult(true, message, SpendLimitType.ACCOUNT_DAY);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class))).willReturn(newArrayList());
        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willThrow(new RuntimeException(message));

        assertThatThrownBy(() -> spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(message);

        reset(spendLimitChecker);

        message = "This is a test exception " + new Random().nextInt();
        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willReturn(txLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_DAY), anyInt()))
                .willThrow(new RuntimeException(message));

        assertThatThrownBy(() -> spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(message);

        reset(spendLimitChecker);
        message = "This is a test exception " + new Random().nextInt();
        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willReturn(txLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_DAY), anyInt()))
                .willReturn(dayLimitResult);
        given(spendLimitChecker.checkDurationLimit(eq(paymentContext), eq(spendLimits),
                anyListOf(ERTransaction.class),
                eq(SpendLimitType.ACCOUNT_MONTH), anyInt()))
                .willThrow(new RuntimeException(message));

        assertThatThrownBy(() -> spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(message);

    }

    @Test
    public void shouldNotReturnNPEWhenSpendLimitCheckerReturnsNullResult() {
        final Account account = anAccount();
        final List<SpendLimit> spendLimits = aStandardSpendLimitList();
        final List<SpendLimit> defaultSpendLimits = aStandardSpendLimitList();
        final PaymentContext paymentContext = aPaymentContext();

        given(spendLimitChecker.checkTransactionLimit(eq(spendLimits), eq(defaultSpendLimits),
                anyListOf(TransactionInfo.class), eq(SpendLimitType.ACCOUNT_TX)))
                .willReturn(null);

        final PaymentApproval approval =
                spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext);

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();

    }

    @Test
    public void shouldNotGetTransactionsWhenNoSpendLimitsOrDefaultSpendLimits() {

        final Account account = anAccount();
        final List<SpendLimit> spendLimits = newArrayList();
        final List<SpendLimit> defaultSpendLimits = newArrayList();
        final PaymentContext paymentContext = aPaymentContext();

        final PaymentApproval approval =
                spendLimitService.checkSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext);

        assertThat(approval).isNotNull();
        assertThat(approval.isSuccess()).isTrue();
        assertThat(approval.getDescription()).isEqualTo("Approved");
        assertThat(approval.getResponseCode()).isEqualTo(1);

        verifyZeroInteractions(erService);

    }

    @Test
    public void shouldCreateTransactionCriteriaCorrectlyWhenNoRenewalTransactions() {
        final ArgumentCaptor<ERTransactionCriteria> criteriaCaptor = ArgumentCaptor.forClass(ERTransactionCriteria.class);
        final Account account = anAccount();
        final List<SpendLimitInfo> defaultSpendLimits = aSpendLimitInfoList();
        final List<ERTransaction> expectedTransactions = ERTransactionDataBuilder.anErTransactionList();
        final PaymentContext paymentContext = aPaymentContext(defaultSpendLimits, new BigDecimal(1.0));
        final LocalDateTime expectedFromDate = LocalDateTime.now().minusDays(10);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class)))
                .willReturn(expectedTransactions);
        given(erDateCalculator.calculateAccountBillingCycleDate(account)).willReturn(expectedFromDate);

        final List<ERTransaction> transactions = spendLimitService.getTransactions(account, paymentContext);

        assertThat(transactions).isEqualTo(expectedTransactions);

        verify(erService).getTransactions(eq(paymentContext), criteriaCaptor.capture());
        verifyNoMoreInteractions(erService);

        final ERTransactionCriteria criteria = criteriaCaptor.getValue();
        assertThat(criteria.getLocale()).isEqualTo(paymentContext.getLocale());
        assertThat(criteria.getChargingId()).isEqualTo(paymentContext.getChargingId());
        assertThat(criteria.getRequiredResultSize()).isNull();
        assertThat(criteria.getFromDate()).isEqualTo(expectedFromDate);
        assertThat(criteria.getTransactionTypes()).containsExactly(PURCHASE.name(), USAGE.name(), REFUND.name());

    }

    @Test
    public void shouldCreateTransactionCriteriaCorrectlyWhenRenewalRuleIncluded() {

        final ArgumentCaptor<ERTransactionCriteria> criteriaCaptor = ArgumentCaptor.forClass(ERTransactionCriteria.class);
        final Account account = anAccount();
        final List<SpendLimitInfo> defaultSpendLimits = aSpendLimitInfoList();
        final List<ERTransaction> expectedTransactions = ERTransactionDataBuilder.anErTransactionList();
        final PaymentContext paymentContext = aPaymentContext(defaultSpendLimits, new BigDecimal(1.0),
                ApprovalCriteria.builder().paymentApprovalRules(newArrayList(PaymentApprovalRule.USE_RENEWAL_TRANSACTIONS))
                        .build());

        final LocalDateTime expectedFromDate = LocalDateTime.now().minusDays(10);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class)))
                .willReturn(expectedTransactions);
        given(erDateCalculator.calculateAccountBillingCycleDate(account)).willReturn(expectedFromDate);

        final List<ERTransaction> transactions = spendLimitService.getTransactions(account, paymentContext);

        assertThat(transactions).isEqualTo(expectedTransactions);

        verify(erService).getTransactions(eq(paymentContext), criteriaCaptor.capture());
        verifyNoMoreInteractions(erService);

        final ERTransactionCriteria criteria = criteriaCaptor.getValue();
        assertThat(criteria.getLocale()).isEqualTo(paymentContext.getLocale());
        assertThat(criteria.getChargingId()).isEqualTo(paymentContext.getChargingId());
        assertThat(criteria.getRequiredResultSize()).isNull();
        assertThat(criteria.getFromDate()).isEqualTo(expectedFromDate);
        assertThat(criteria.getTransactionTypes()).containsExactly(PURCHASE.name(), USAGE.name(), REFUND.name(), RENEWAL.name());
    }

    @Test
    public void shouldRespondSuccessfullyWhenApprovalCriteriaIsNull() {
        final ArgumentCaptor<ERTransactionCriteria> criteriaCaptor = ArgumentCaptor.forClass(ERTransactionCriteria.class);
        final Account account = anAccount();
        final List<SpendLimitInfo> defaultSpendLimits = aSpendLimitInfoList();
        final List<ERTransaction> expectedTransactions = ERTransactionDataBuilder.anErTransactionList();
        final PaymentContext paymentContext = aPaymentContext(defaultSpendLimits, new BigDecimal(1.0), null);
        final LocalDateTime expectedFromDate = LocalDateTime.now().minusDays(10);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class)))
                .willReturn(expectedTransactions);
        given(erDateCalculator.calculateAccountBillingCycleDate(account)).willReturn(expectedFromDate);

        final List<ERTransaction> transactions = spendLimitService.getTransactions(account, paymentContext);

        assertThat(transactions).isEqualTo(expectedTransactions);

        verify(erService).getTransactions(eq(paymentContext), criteriaCaptor.capture());
        verifyNoMoreInteractions(erService);

        final ERTransactionCriteria criteria = criteriaCaptor.getValue();
        assertThat(criteria.getLocale()).isEqualTo(paymentContext.getLocale());
        assertThat(criteria.getChargingId()).isEqualTo(paymentContext.getChargingId());
        assertThat(criteria.getRequiredResultSize()).isNull();
        assertThat(criteria.getFromDate()).isEqualTo(expectedFromDate);
        assertThat(criteria.getTransactionTypes()).containsExactly(PURCHASE.name(), USAGE.name(), REFUND.name());
    }

    @Test
    public void shouldRespondSuccessfullyWhenPaymentApprovalRulesAreNull() {
        final ArgumentCaptor<ERTransactionCriteria> criteriaCaptor = ArgumentCaptor.forClass(ERTransactionCriteria.class);
        final Account account = anAccount();
        final List<SpendLimitInfo> defaultSpendLimits = aSpendLimitInfoList();
        final List<ERTransaction> expectedTransactions = ERTransactionDataBuilder.anErTransactionList();
        final PaymentContext paymentContext = aPaymentContext(defaultSpendLimits, new BigDecimal(1.0), ApprovalCriteria.builder().build());
        final LocalDateTime expectedFromDate = LocalDateTime.now().minusDays(10);

        given(erService.getTransactions(eq(paymentContext), any(ERTransactionCriteria.class)))
                .willReturn(expectedTransactions);
        given(erDateCalculator.calculateAccountBillingCycleDate(account)).willReturn(expectedFromDate);

        final List<ERTransaction> transactions = spendLimitService.getTransactions(account, paymentContext);

        assertThat(transactions).isEqualTo(expectedTransactions);

        verify(erService).getTransactions(eq(paymentContext), criteriaCaptor.capture());
        verifyNoMoreInteractions(erService);

        final ERTransactionCriteria criteria = criteriaCaptor.getValue();
        assertThat(criteria.getLocale()).isEqualTo(paymentContext.getLocale());
        assertThat(criteria.getChargingId()).isEqualTo(paymentContext.getChargingId());
        assertThat(criteria.getRequiredResultSize()).isNull();
        assertThat(criteria.getFromDate()).isEqualTo(expectedFromDate);
        assertThat(criteria.getTransactionTypes()).containsExactly(PURCHASE.name(), USAGE.name(), REFUND.name());
    }

}
