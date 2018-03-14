package com.vodafone.charging.accountservice.domain;

import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class EnrichedAccountInfoTest {



    @Test
    public void shouldBuildImmutableObject() {

        List<String> usergroups = newArrayList("test-ug1", "test-ug2");
        EnrichedAccountInfo accountInfo = new EnrichedAccountInfo.Builder("success")
                .accountId("accountId")
                .ban("test-ban")
                .billingCycleDay(22)
                .childServiceProviderId("test-childServiceProviderId")
                .customerType("PRE")
                .serviceProviderId("test-serviceProviderId")
                .serviceProviderType("test-serviceProviderType")
                .usergroups(usergroups)
                .errorDescription("test-errorDescription")
                .errorId("test-errorId")
                .build();

        assertThat(accountInfo.getAccountId()).isEqualTo("accountId");
        assertThat(accountInfo.getValidationStatus()).isEqualTo("success");
        assertThat(accountInfo.getBan()).isEqualTo("test-ban");
        assertThat(accountInfo.getBillingCycleDay()).isEqualTo(22);
        assertThat(accountInfo.getChildServiceProviderId()).isEqualTo("test-childServiceProviderId");
        assertThat(accountInfo.getCustomerType()).isEqualTo("PRE");
        assertThat(accountInfo.getServiceProviderId()).isEqualTo("test-serviceProviderId");
        assertThat(accountInfo.getServiceProviderType()).isEqualTo("test-serviceProviderType");
        assertThat(accountInfo.getUsergroups()).isEqualTo(usergroups);
        assertThat(accountInfo.getErrorDescription()).isEqualTo("test-errorDescription");
        assertThat(accountInfo.getErrorId()).isEqualTo("test-errorId");
    }
}
