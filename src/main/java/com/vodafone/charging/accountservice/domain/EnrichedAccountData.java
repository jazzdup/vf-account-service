package com.vodafone.charging.accountservice.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EnrichedAccountData {

    private UUID id;
    private boolean isSuccess;
    private String accountId;
    private List<String> usergroups;

    public EnrichedAccountData() {
    }

    public EnrichedAccountData(boolean isSuccess, String accountId, List<String> usergroups) {
        this.id = UUID.randomUUID();
        this.isSuccess = isSuccess;
        this.accountId = accountId;
        this.usergroups = usergroups;
    }

    public UUID getUuid() {
        return id;
    }

    public boolean getSuccess() {
        return isSuccess;
    }

    public List<String> getUsergroups() {
        return usergroups;
    }

    public static class Builder {

        private boolean isSuccess;
        private String accountId;
        private List<String> usergroups;

        public Builder result(final boolean result) {
            this.isSuccess = result;
            return this;
        }
        public Builder usergroups(final List<String> usergroups) {
            this.usergroups = usergroups;
            return this;
        }

        public EnrichedAccountData build() {
            return new EnrichedAccountData(this.isSuccess, this.accountId, this.usergroups);
        }

    }

}
