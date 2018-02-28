package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@Slf4j
public class MongoIT {
    @Autowired
    AccountRepository repository;

    @Test
    public void shouldSaveAndFindByChargingId() throws Exception {
        repository.deleteAll();
        final Account expectedAccount = anAccount();
        final ChargingId expectedChargingId = expectedAccount.getChargingId();
        repository.save(expectedAccount);
        final Account account = repository.findByChargingId(expectedChargingId);
        assertThat(account).isEqualToComparingFieldByFieldRecursively(expectedAccount);
        final List l = repository.findAll();
        assertThat(l.size()).isEqualTo(1);
    }
}
