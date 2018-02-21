package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.PaymentValidation;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SpendLimitService {

    @Autowired
    private AccountRepository repository;

    //TODO
    //Get Account Profile Info - see if Spend Limit applies
    //Call DecouplingAdapter to get transactions
    //Calculate whether SpendLimit is Breached
    //Respond with a SpendLimit response
    public PaymentValidation paymentValidation() {
        throw new UnsupportedOperationException();
    }

    //TODO
    //Spend Limits
    public Account updateSpendLimits(final String accountId, final List<SpendLimitInfo> spendLimitInfo) {
        //TODO - gets the account record and creates or replaces the spendLimit info on there.

        List<SpendLimit> limits = spendLimitInfo.stream()
                .filter(Objects::nonNull)
                .map(limit -> SpendLimit.builder()
                        .active(limit.isActive())
                        .spendLimitType(limit.getSpendLimitType())
                        .limit(limit.getLimit()).build()
                ).collect(Collectors.toList());

        final Account account = repository.findOne(accountId);
        account.getProfiles().stream()
                .findFirst()
                .ifPresent(profile -> {
                    profile.setSpendLimits(limits);
                    repository.save(account);
                });

        return account;

    }


}
