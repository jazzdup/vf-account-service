package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.Profile;
import com.vodafone.charging.accountservice.domain.model.Transaction;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;

public class MongoData {
    public static Date aFixedDate(){
        return new GregorianCalendar(2018, 1, 1, 1, 1, 1).getTime();
    }
    public static Account anAccount(){
        return Account.builder()
                .id("1")
                .lastValidate(aFixedDate())
                .chargingId(aChargingId("100"))
                .customerType("PRE")
                .profiles(Arrays.asList(aProfile()))
                .build();
    }
    public static Profile aProfile(){
        return Profile.builder()
                .userGroups(Arrays.asList("ug1", "ug2"))
                .lastUpdatedTransactions(aFixedDate())
                .lastUpdatedUserGroups(aFixedDate())
                .transactions(Arrays.asList(aTransaction()))
                .build();
    }
    public static Transaction aTransaction(){
        return Transaction.builder()
                .erTransactionId(10)
                .id(100)
                .build();
    }
}
