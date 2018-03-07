package com.vodafone.charging.accountservice.domain.model;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.*;

@Entity
@Getter @ToString
@Document(collection = "account")
public class Account {
    @Id
    private String id;
    private ChargingId chargingId;
    private Date lastValidate;
    private String customerType;//TODO: add constraint PRE/POST
    private int billingCycleDay;
    private List<Profile> profiles;

    public Account(){}

    public Account(String id, ChargingId chargingId, Date lastValidate, String customerType, int billingCycleDay, List<Profile> profiles) {
        this.id = id;
        this.chargingId = chargingId;
        this.lastValidate = lastValidate;
        this.customerType = customerType;
        this.billingCycleDay = billingCycleDay;
        this.profiles = profiles;
    }

    public Account(ChargingId chargingId, EnrichedAccountInfo info, Date lastValidate){
        this.chargingId = chargingId;
        this.lastValidate = lastValidate;
        this.customerType = info.getCustomerType();
        this.billingCycleDay = info.getBillingCycleDay();
        Profile profile = Profile.builder()
                .userGroups(info.getUsergroups())
                .build();
        this.profiles = Arrays.asList(profile);
    }

    public Map<String, Object> asMap() throws IllegalAccessException {
        Map<String, Object> values = new HashMap<>();

        Field[] fieldsArr = this.getClass().getDeclaredFields();

        for (Field field : fieldsArr) {
            values.put(field.getName(), field.get(this));
        }
        return values;
    }

    public static AccountBuilder builder() {
        return new AccountBuilder();
    }

    public static class AccountBuilder {
        private String id;
        private ChargingId chargingId;
        private Date lastValidate;
        private String customerType;
        private int billingCycleDay;
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

        public AccountBuilder billingCycleDay(int billingCycleDay) {
            this.billingCycleDay = billingCycleDay;
            return this;
        }

        public AccountBuilder profiles(List<Profile> profiles) {
            this.profiles = profiles;
            return this;
        }

        public Account build() {
            return new Account(id, chargingId, lastValidate, customerType, billingCycleDay, profiles);
        }

        @Override
        public String toString() {
            return "AccountBuilder{" +
                    "id='" + id + '\'' +
                    ", chargingId=" + chargingId +
                    ", lastValidate=" + lastValidate +
                    ", customerType='" + customerType + '\'' +
                    ", billingCycleDay=" + billingCycleDay +
                    ", profiles=" + profiles +
                    '}';
        }

    }
}
