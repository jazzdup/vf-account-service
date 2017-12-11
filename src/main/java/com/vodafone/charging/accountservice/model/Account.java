package com.vodafone.charging.accountservice.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Locale;

@Repository
public class Account {

    private String id;
    private Locale locale;

    @Autowired
    private Validation validation;

    public Account() {
    }

    private Account(final String id, final Locale locale, final Validation validation) {
        this.id = id;
        this.locale = locale;
        this.validation = validation;
    }

    public String getId() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public Validation getValidation() {
        return validation;
    }

    public static class Builder {
        private String accountId;
        private Locale locale;
        private Validation validation;

        public Builder msisdn(final String accountId) {
            this.accountId = accountId;
            return this;
        }
        public Builder locale(final Locale locale) {
            this.locale = locale;
            return this;
        }
        public Builder locale(final Validation validation) {
            this.validation = validation;
            return this;
        }

        public Account build() {
            return new Account(accountId, locale, validation);
        }
    }

}
