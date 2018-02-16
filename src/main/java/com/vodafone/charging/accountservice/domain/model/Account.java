package com.vodafone.charging.accountservice.domain.model;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter @ToString
public class Account {
    @Id
    private String id;
    private ChargingId chargingId;
    private Date lastValidate;
    private String customerType;//PRE/POST
    private List<Profile> profiles;

    public Account(){}

    public Account(String id, ChargingId chargingId, Date lastValidate, String customerType, List<Profile> profiles) {
        this.id = id;
        this.chargingId = chargingId;
        this.lastValidate = lastValidate;
        this.customerType = customerType;
        this.profiles = profiles;
    }

    public Account(ChargingId chargingId, EnrichedAccountInfo info){
        this.chargingId = chargingId;
        this.lastValidate = new Date();
        this.customerType = info.getCustomerType();
        Profile profile = Profile.builder()
                .userGroups(info.getUsergroups())
                .build();
        this.profiles = Arrays.asList(profile);
    }

    public static AccountBuilder builder() {
        return new AccountBuilder();
    }

    public static class AccountBuilder {
        private String id;
        private ChargingId chargingId;
        private Date lastValidate;
        private String customerType;
        private List<Profile> profiles;

        AccountBuilder() {
        }

        public AccountBuilder id(String id) {
            this.id = id;
            return this;
        }

        public AccountBuilder chargingId(ChargingId chargingId) {
            this.chargingId = chargingId;
            return this;
        }

        public AccountBuilder lastValidate(Date lastValidate) {
            this.lastValidate = lastValidate;
            return this;
        }

        public AccountBuilder customerType(String customerType) {
            this.customerType = customerType;
            return this;
        }

        public AccountBuilder profiles(List<Profile> profiles) {
            this.profiles = profiles;
            return this;
        }

        public Account build() {
            return new Account(id, chargingId, lastValidate, customerType, profiles);
        }

        public String toString() {
            return "Account.AccountBuilder(id=" + this.id + ", chargingId=" + this.chargingId + ", lastValidate=" + this.lastValidate + ", customerType=" + this.customerType + ", profiles=" + this.profiles + ")";
        }
    }
}
