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
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionCriteria;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    //TODO
            /* --- FEATURE ---
        - get account using accountId -----> done
            if not found then return validationFailed -----> done
        - see if account has a spend limit associated with it -----> done
                - if not then see if request has a default spend limit ----> done
                        - if not the return a validationFailed ----> done
                        - if yes then get the transactions for the account for the last month
                          Calculate whether spend limit breached
                          - if not then return validationSuccess
                          - if breached return validationFailed
        - if yes then get the transactions for the account for the last month.
            Calculate whether the limit is breached for any of the limits set.
            - if not then return validationSuccess
                - if breached return validationFailed
         */
    //Respond with a PaymentValidation response i.e. paymentValidated / paymentNotValidated

    public PaymentApproval approvePayment(final String accountId, final PaymentContext paymentContext) {

        //TODO Get the SpendLimits info for the accountId.  If no account, fail, if no SpendLimit
        //Get record
        final Account account = ofNullable(repository.findOne(accountId))
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No account found using id " + accountId));

        final Profile profile = account.getProfiles().stream().findFirst()
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No Profile found using account id " + accountId));

        final List<SpendLimit> spendLimits = ofNullable(profile.getSpendLimits()).orElse(newArrayList());
        final List<SpendLimitInfo> defaultSpendLimits = ofNullable(paymentContext.getCatalogInfo().getDefaultSpendLimitInfo()).orElse(newArrayList());

        return calculateSpendLimits(account, spendLimits, defaultSpendLimits, paymentContext);
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

    public PaymentApproval calculateSpendLimits(final Account account, final List<SpendLimit> spendLimits,
                                                final List<SpendLimitInfo> defaultSpendLimitInfos,
                                                final PaymentContext paymentContext) {

        boolean spendLimitBreached = false;
        //Create a TransactionSearchCriteria - TransactionTypes, TransactionsPeriod(convenience methods of month)
        //Get Transactions from ER Core
        //Calculate Limits if you get any Transactions
        final List<String> transactionTypes = newArrayList(PURCHASE.name(), USAGE.name(), REFUND.name(), RENEWAL.name());
        final ERTransactionCriteria criteria = ERTransactionCriteria.builder().monetaryOnly(true)
                .transactionTypes(transactionTypes)
                .fromDate(calculateTransactionFromDate(account))
                .toDate(LocalDateTime.now().withDayOfMonth(1))
                .build();
        final List<SpendLimit> defaultSpendLimits = SpendLimit.fromSpendLimitsInfo(defaultSpendLimitInfos);
        final List<ERTransaction> erTransactions = erService.getTransactions(paymentContext, criteria);

        //Go through all limit types and check if breached

        List<SpendLimitResult> results = Stream.of(SpendLimitType.values()).map(spendLimitType -> {
            SpendLimitResult result = null;
            if (spendLimitType.equals(SpendLimitType.ACCOUNT_TX)) {
                result = spendLimitChecker.checkTransactionLimit(spendLimits, defaultSpendLimits, newArrayList(paymentContext.getTransactionInfo()));
            } else if (spendLimitType.equals(SpendLimitType.ACCOUNT_DAY)) {
                result = spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, erTransactions,
                        paymentContext.getTransactionInfo().getAmount(), SpendLimitType.ACCOUNT_DAY);
            } else if (spendLimitType.equals(SpendLimitType.ACCOUNT_MONTH)) {
                result = checkMonthLimit(spendLimits, defaultSpendLimits, erTransactions);
            }
            return result;
        }).collect(Collectors.toList());

        //TODO Map result to approval response
        final Optional<SpendLimitResult> failures = results.stream().filter(result -> !result.isSuccess()).findFirst();

        PaymentApproval approval;

        if (failures.isPresent()) {
            approval = PaymentApproval.builder().success(false)
                    .responseCode(2)
                    .description("Spend Limit breached.").build();
        } else {
            approval = PaymentApproval.builder().success(true)
                    .responseCode(2)
                    .description("Spend Limit breached.").build();
        }
        return approval;
    }

    //We only want max of a month but we must consider the billingCycleDay for Post pay customers.
    public LocalDateTime calculateTransactionFromDate(Account account) {
        int billingCycleDay = 1;
        //TODO store and deal with billingCycleDate.  From date should be from the last billingCycle date
//      int billingCycleDay = nullableOf(account.billingCycleDay).orElse(1);

        final LocalDate firstOfMonth = LocalDate.now(ZoneId.of("CET")).withDayOfMonth(billingCycleDay);
        return LocalDateTime.of(firstOfMonth, LocalTime.MIDNIGHT);
    }

    public SpendLimitResult checkMonthLimit(List<SpendLimit> spendLimits, List<SpendLimit> defaultSpendLimits, List<ERTransaction> tx) {

        final Optional<BigDecimal> totalTxAmount = tx.stream().map(ERTransaction::getAmount)
                .reduce(BigDecimal::add);

        final List<SpendLimit> limits = spendLimits.stream()
                .filter(l -> l.getSpendLimitType().equals(SpendLimitType.ACCOUNT_MONTH))
                .collect(Collectors.toList());

        if (totalTxAmount.isPresent() && !limits.isEmpty() && limits.size() == 1
                && limits.get(0).getLimit() < Double.valueOf(totalTxAmount.get().toString())) {
            return SpendLimitResult.builder().success(false).build();
        } else {
            final List<SpendLimit> defaultLimits = defaultSpendLimits.stream()
                    .filter(l -> l.getSpendLimitType().equals(SpendLimitType.ACCOUNT_MONTH))
                    .collect(Collectors.toList());

            if (!defaultLimits.isEmpty() && defaultLimits.size() != 1
                    && defaultLimits.get(0).getLimit() < Double.valueOf(totalTxAmount.toString())) {
                return SpendLimitResult.builder().success(false).build();
            }
        }
        return SpendLimitResult.builder().success(true).build();

    }

}
