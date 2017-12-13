package com.vodafone.charging.accountservice.domain;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Account {

    private String id;
    private Locale locale;
    private ChargingId chargingId;

    private Account() {
    }

    public Account(final String id, final Locale locale, final ChargingId chargingId) {
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

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", locale=" + locale +
                ", chargingId=" + chargingId +
                '}';
    }
}
