package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.PaymentValidation;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpendLimitService {


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
    public void putSpendLimits(final String accountId, final List<SpendLimitInfo> spendLimitInfo) {
        //TODO - gets the account record and creates or replaces the spendLimit info on there.
        throw new UnsupportedOperationException();
    }

}
