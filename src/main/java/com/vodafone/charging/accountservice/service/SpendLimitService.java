package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.domain.PaymentValidation;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.Profile;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.exception.RepositoryResourceNotFoundException;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class SpendLimitService {


    private AccountRepository repository;

    @Autowired
    public SpendLimitService(AccountRepository repository) {
        this.repository = repository;
    }

    //TODO
    //Get Account Info - see if Spend Limit applies
    //Call DecouplingAdapter to get transactions
    //Calculate whether SpendLimit is Breached
    //Respond with a PaymentValidation response i.e. paymentValidated / paymentNotValidated
    public PaymentValidation validatePayment(String accountId, PaymentContext paymentContext) {

        //TODO Get the SpendLimits info for the accountId.  If no account, fail, if no SpendLimit
        //Get the transactions
        throw new UnsupportedOperationException();
    }

    public Account updateSpendLimits(final String accountId, final List<SpendLimitInfo> spendLimitInfo) {
        final List<SpendLimit> limits = SpendLimit.fromSpendLimitInfo(spendLimitInfo);
        final Account account = ofNullable(repository.findOne(accountId))
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No account found using id " + accountId));

        final Profile profile = account.getProfiles().stream().findFirst()
                .orElseThrow(() -> new RepositoryResourceNotFoundException("No Profile not found using account id " + accountId));

        profile.setSpendLimits(limits);

        return repository.save(account);
    }
}
