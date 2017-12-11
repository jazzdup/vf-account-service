package com.vodafone.charging.accountservice.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Locale;

@Repository
public class Account {

    private String accountId;
    private Locale locale;

    @Autowired
    private Validation validation;

    public String getAccountId() {
        return accountId;
    }

    public Locale getLocale() {
        return locale;
    }

    public Validation getValidation() {
        return validation;
    }

}
