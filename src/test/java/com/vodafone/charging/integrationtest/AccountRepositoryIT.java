package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.mongo.AbstractMongoTest;
import com.vodafone.charging.accountservice.mongo.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.vodafone.charging.data.builder.MongoData.anAccount;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
public class AccountRepositoryIT extends AbstractMongoTest {

    @Autowired
    private AccountRepository repository;

    @Test
    public void testRepositoryCRUD(){
        repository.deleteAll();

        Account expectedAccount = anAccount();
        repository.save(expectedAccount);
        List<Account> accountList = repository.findAll();
        assertThat(accountList.size()).isEqualTo(1);
        Account account = accountList.get(0);
        assertThat(account).isEqualToComparingFieldByFieldRecursively(expectedAccount);

        Account account2 = repository.findOne(anAccount().getId());
        assertThat(account2).isEqualToComparingFieldByFieldRecursively(expectedAccount);

    }
}
