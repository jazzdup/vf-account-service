package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.PaymentValidation;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpendLimitService {

    @Autowired
    private AccountRepository repository;

    //TODO
    //Get Account Info - see if Spend Limit applies
    //Call DecouplingAdapter to get transactions
    //Calculate whether SpendLimit is Breached
    //Respond with a PaymentValidation response i.e. paymentValidated / paymentNotValidated
    public PaymentValidation paymentValidation() {
        throw new UnsupportedOperationException();
    }

    public Account updateSpendLimits(final String accountId, final List<SpendLimitInfo> spendLimitInfo) {
        final List<SpendLimit> limits = SpendLimit.fromSpendLimitInfo(spendLimitInfo);
        final Account account = Optional.ofNullable(repository.findOne(accountId))
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No account found with id " + accountId));
        account.getProfiles().stream()
                .findFirst()
                .ifPresent(profile -> {
                    profile.setSpendLimits(limits);
                    repository.save(account);
                });

        return account;
    }
}
