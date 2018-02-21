package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.domain.model.Profile;
import com.vodafone.charging.accountservice.domain.model.Transaction;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;

public class MongoDataBuilder {
    public static Date aFixedDate(){
        return new GregorianCalendar(2018, 1, 1, 1, 1, 1).getTime();
    }
    public static Account anAccount(){
        return Account.builder()
                .id(String.valueOf(new Random().nextInt()))
                .lastValidate(aFixedDate())
                .chargingId(aChargingId())
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
                .erTransactionId(new Random().nextLong())
                .id(new Random().nextLong())
                .build();
    }
}
