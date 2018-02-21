package com.vodafone.charging.accountservice.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Builder
@Getter @ToString
public class Profile {
    private List<String> userGroups;
    private Date lastUpdatedUserGroups;
    private List<Transaction> transactions;
    private Date lastUpdatedTransactions;
    private SpendLimits spendLimits;
//    private List<Subscriptions> subscriptions;
}
