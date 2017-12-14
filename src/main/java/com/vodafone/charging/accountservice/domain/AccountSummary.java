package com.vodafone.charging.accountservice.domain;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Represents Vodafone account information from requesting clients
 */
@Component
public class AccountSummary {

    private String id;
    private Locale locale;
    private ChargingId chargingId;

    private AccountSummary() {
    }

    public AccountSummary(final String id, final Locale locale, final ChargingId chargingId) {
        this.id = id;
        this.locale = locale;
        this.chargingId = chargingId;
    }

    public String getId() {
        return id;
    }

    public Locale getLocale() {
        return locale;
    }

    public ChargingId getChargingId() {
        return chargingId;
    }

}
