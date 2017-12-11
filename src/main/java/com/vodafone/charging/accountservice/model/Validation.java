package com.vodafone.charging.accountservice.model;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Validation {

    private String accountId;
    private List<String> usergroups;

    public Validation() {
    }

    public Validation(String accountId, List<String> usergroups) {
        this.accountId = accountId;
        this.usergroups = usergroups;
    }

    public String getAccountId() {
        return accountId;
    }

    public List<String> getUsergroups() {
        return usergroups;
    }

    public static class Builder {

        private String accountId;
        private List<String> usergroups;

        public Builder accountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }
        public Builder usergroups(final List<String> usergroups) {
            this.usergroups = usergroups;
            return this;
        }

        public Validation build(final String accountId, final List<String> usergroups) {
            return new Validation(accountId, usergroups);
        }

    }




}
