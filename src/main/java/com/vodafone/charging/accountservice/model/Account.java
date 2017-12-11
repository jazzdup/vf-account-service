package com.vodafone.charging.accountservice.model;

import org.springframework.stereotype.Repository;

import java.util.Locale;

@Repository
public class Account {

    private String id;
    private Locale locale;
    //TODO add ChargingId in here
    //ChargingId chargingId

    public Account() {
    }

    private Account(final String id, final Locale locale) {
        this.id = id;
        this.locale = locale;
    }

    public String getId() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public static class Builder {
        private String accountId;
        private Locale locale;

        public Builder accountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }
        public Builder locale(final Locale locale) {
            this.locale = locale;
            return this;
        }

        public Account build() {
            return new Account(accountId, locale);
        }
    }

}
