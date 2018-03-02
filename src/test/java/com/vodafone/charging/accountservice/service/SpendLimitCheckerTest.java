package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.TestBeanConfiguration;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.domain.enums.SpendLimitType.ACCOUNT_TX;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimit;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TestBeanConfiguration.class)
public class SpendLimitCheckerTest extends SpendLimitCheckerBase {

    @Test
    public void shouldNotBreachWhenTxLimitDefinedAndCurrentTxIsEqualToTxLimit() {
        //given
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(BigDecimal.valueOf(2.0))
                        .build());

        //when
        SpendLimitResult result =
                spendLimitChecker.checkTransactionLimit(spendLimits, defaultSpendLimits,
                        transactionInfo,
                        SpendLimitType.ACCOUNT_TX);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(0).getLimit());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(transactionInfo.get(0).getAmount().doubleValue());

    }

    @Test
    public void shouldBreachWhenTxLimitDefinedAndCurrentTxIsOverTxLimit() {
        //given
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(BigDecimal.valueOf(2.1))
                        .build());

        //when
        SpendLimitResult result =
                spendLimitChecker.checkTransactionLimit(spendLimits, defaultSpendLimits,
                        transactionInfo,
                        SpendLimitType.ACCOUNT_TX);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_TX);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_TX.name());
        assertThat(result.getFailureReason()).contains("spend limit breached");
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(0).getLimit());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(transactionInfo.get(0).getAmount().doubleValue());
    }

    @Test
    public void shouldNotBreachDefaultWhenNoTxLimitDefinedAndAccountTxUnderTxLimit() {

        //given
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(BigDecimal.valueOf(3.1))
                        .build());

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkTransactionLimit(Lists.emptyList(), defaultSpendLimits,
                        transactionInfo,
                        SpendLimitType.ACCOUNT_TX);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getAppliedLimitValue()).isEqualTo(defaultSpendLimits.get(0).getLimit());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(transactionInfo.get(0).getAmount().doubleValue());

    }

    @Test
    public void shouldBreachDefaultWhenNoTxLimitDefinedAndTxIsOverTxLimit() {
        //given
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(BigDecimal.valueOf(5.1))
                        .build());

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkTransactionLimit(Lists.emptyList(), defaultSpendLimits,
                        transactionInfo,
                        SpendLimitType.ACCOUNT_TX);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_TX);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_TX.name());
        assertThat(result.getFailureReason()).contains(" default spend limit breached");
        assertThat(result.getAppliedLimitValue()).isEqualTo(defaultSpendLimits.get(0).getLimit());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(transactionInfo.get(0).getAmount().doubleValue());

    }

    @Test
    public void shouldNotBreachWhenUnderTxLimitButOverDefaultLimit() {
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(BigDecimal.valueOf(2.0))
                        .build());

        final SpendLimitResult result =
                spendLimitChecker.checkTransactionLimit(spendLimits, newArrayList(aSpendLimit(0.2, ACCOUNT_TX)),
                        transactionInfo,
                        SpendLimitType.ACCOUNT_TX);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(0).getLimit());
        assertThat(result.getAppliedLimitValue()).isNotEqualTo(defaultSpendLimits.get(0).getLimit());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(transactionInfo.get(0).getAmount().doubleValue());
    }

    @Test
    public void shouldNotBreachWhenNoTxLimitOrDefaultDefined() {

        //given
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(BigDecimal.valueOf(5000))
                        .build());

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkTransactionLimit(Lists.emptyList(), Lists.emptyList(),
                        transactionInfo,
                        SpendLimitType.ACCOUNT_TX);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getAppliedLimitValue()).isEqualTo(0.0);
        assertThat(result.getTotalTransactionsValue()).isEqualTo(transactionInfo.get(0).getAmount().doubleValue());
    }

}
