package com.vodafone.charging.accountservice.model;

import java.util.List;

public class Account {

    private String accountId;
    private List<String> usergroups;

    public String getAccountId() {
        return accountId;
    }

    public List<String> getUsergroups() {
        return usergroups;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", usergroups=" + usergroups +
                '}';
    }
}
