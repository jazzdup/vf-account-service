package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.client.ERService;
import com.vodafone.charging.accountservice.domain.PaymentApproval;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.Profile;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.client.CatalogInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.dto.er.ERTransactionType.*;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class SpendLimitService {

    private AccountRepository repository;
    private ERService erService;
    private SpendLimitChecker spendLimitChecker;

    @Autowired
    public SpendLimitService(AccountRepository repository, ERService erService, SpendLimitChecker spendLimitChecker) {
        this.repository = repository;
        this.erService = erService;
        this.spendLimitChecker = spendLimitChecker;
    }

    public PaymentApproval approvePayment(final String accountId, final PaymentContext paymentContext) {

        //TODO Get the SpendLimits info for the accountId.  If no account, fail, if no SpendLimit
        //Get record
        final Account account = ofNullable(repository.findOne(accountId))
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No account found using id " + accountId));

        final Profile profile = account.getProfiles().stream().findFirst()
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No Profile found using account id " + accountId));

        final List<SpendLimit> spendLimits = ofNullable(profile.getSpendLimits()).orElse(newArrayList());
        final Optional<CatalogInfo> catalogInfoOpt = ofNullable(paymentContext.getCatalogInfo());
        List<SpendLimitInfo> defaultSpendLimits = catalogInfoOpt.map(CatalogInfo::getDefaultSpendLimitInfo).orElse(newArrayList());

        return checkSpendLimits(account, spendLimits, SpendLimit.fromSpendLimitsInfo(defaultSpendLimits), paymentContext);
    }

    public Account updateSpendLimits(final String accountId, final List<SpendLimitInfo> spendLimitInfos) {
        final List<SpendLimit> limits = SpendLimit.fromSpendLimitsInfo(spendLimitInfos);
        final Account account = ofNullable(repository.findOne(accountId))
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No account found using id " + accountId));

        final Profile profile = account.getProfiles().stream().findFirst()
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No Profile not found using account id " + accountId));

        profile.setSpendLimits(limits);

        return repository.save(account);
    }

    public PaymentApproval checkSpendLimits(@NonNull final Account account,
                                            @NonNull final List<SpendLimit> spendLimits,
                                            @NonNull final List<SpendLimit> defaultSpendLimits,
                                            @NonNull final PaymentContext paymentContext) {
        //No need to continue if no spend limits configured
        if (spendLimits.isEmpty() && defaultSpendLimits.isEmpty()) {
            return createResponse(newArrayList(SpendLimitResult.builder().success(true).build()));
        }

        int billingCycleDay = ofNullable(account.getBillingCycleDay()).orElse(1);
        final List<ERTransaction> erTransactions = getHistoricTransactions(account, paymentContext);

        final List<SpendLimitResult> results = newArrayList();

        for (SpendLimitType type : SpendLimitType.values()) {
            SpendLimitResult result = null;
            if (type.equals(SpendLimitType.ACCOUNT_TX)) {
                result = spendLimitChecker.checkTransactionLimit(spendLimits, defaultSpendLimits,
                        newArrayList(paymentContext.getTransactionInfo()), SpendLimitType.ACCOUNT_TX);
            } else if (type.equals(SpendLimitType.ACCOUNT_DAY)) {
                result = spendLimitChecker.checkDurationLimit(paymentContext, spendLimits,
                        erTransactions, SpendLimitType.ACCOUNT_DAY, billingCycleDay);
            } else if (type.equals(SpendLimitType.ACCOUNT_MONTH)) {
                result = spendLimitChecker.checkDurationLimit(paymentContext, spendLimits,
                        erTransactions, SpendLimitType.ACCOUNT_MONTH, billingCycleDay);
            }

            if (Objects.nonNull(result) && !result.isSuccess()) {
                results.add(result);
                break;
            }
        }
        return createResponse(results);
    }

    public List<ERTransaction> getHistoricTransactions(Account account, PaymentContext paymentContext) {

        final List<String> transactionTypes = newArrayList(PURCHASE.name(), USAGE.name(), REFUND.name(), RENEWAL.name());
        final ERTransactionCriteria criteria = ERTransactionCriteria.builder().monetaryOnly(true)
                .transactionTypes(transactionTypes)
                .fromDate(calculateTransactionFromDate(account))
                .toDate(LocalDateTime.now())
                .build();

        return erService.getTransactions(paymentContext, criteria);
    }

    //We only want max of a month but we must consider the billingCycleDay for Post pay customers.
    public LocalDateTime calculateTransactionFromDate(Account account) {
        int billingCycleDay = 1;
        //TODO store and deal with billingCycleDate.  From date should be from the last billingCycle date
//      int billingCycleDay = nullableOf(account.billingCycleDay).orElse(1);

        final LocalDate firstOfMonth = LocalDate.now(ZoneId.of("CET")).withDayOfMonth(billingCycleDay);
        return LocalDateTime.of(firstOfMonth, LocalTime.MIDNIGHT);
    }

    private PaymentApproval createResponse(List<SpendLimitResult> results) {

        //TODO Map result to approval response
        final Optional<SpendLimitResult> failure = results.stream().filter(result -> !result.isSuccess()).findFirst();

        PaymentApproval approval;

        if (failure.isPresent()) {
            approval = PaymentApproval.builder().success(failure.get().isSuccess())
                    .responseCode(2)
                    .description(failure.get().getFailureReason()).build();
        } else {
            approval = PaymentApproval.builder().success(true)
                    .responseCode(1)
                    .description("Approved").build();
        }
        return approval;

    }

}
