package com.vodafone.charging.accountservice.repository;

import com.vodafone.charging.accountservice.domain.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String>{
}
