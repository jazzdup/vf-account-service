package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.model.Transaction;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class MongoDataBuilder {
    public static Date aFixedDate(){
        return new GregorianCalendar(2018, 1, 1, 1, 1, 1).getTime();
    }

    public static Transaction aTransaction(){
        return Transaction.builder()
                .erTransactionId(new Random().nextLong())
                .id(new Random().nextLong())
                .build();
    }

}
