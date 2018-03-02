package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.TestBeanConfiguration;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TestBeanConfiguration.class)
public class SpendLimitCheckerTest extends SpendLimitCheckerBase {



    @Test
    public void shouldNotBreachWhenTxLimitDefinedAndAccountTxIsEqualToTxLimit() {
        //given
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(new BigDecimal(2.0))
                        .build());

        //when
        SpendLimitResult success = spendLimitChecker.checkTransactionLimit(spendLimits, defaultSpendLimits, transactionInfo);

        //then
        assertThat(success.isSuccess()).isTrue();

    }

    public void shouldBreachWhenTxLimitDefinedAndAccountTxIsOverTxLimit() {
    }

    public void shouldNotBreachDefaultWhenNoTxLimitDefinedAndAccountTxUnderTxLimit() {
    }

    public void shouldBreachDefaultWhenNoTxLimitDefinedDefinedAndAccountTxIsOverTxLimit() {
    }

}
