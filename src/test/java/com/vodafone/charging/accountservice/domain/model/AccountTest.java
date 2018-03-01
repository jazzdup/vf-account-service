package com.vodafone.charging.accountservice.domain.model;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static com.vodafone.charging.data.builder.MongoDataBuilder.aFixedDate;
import static com.vodafone.charging.data.builder.ProfileDataBuilder.aProfile;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountTest {

    @Test
    public void checkConstructors() {
        final ChargingId chargingId = aChargingId();
        final Date date = aFixedDate();
        final Profile profile = aProfile();
        final Account account = new Account("id", chargingId, date, "type", 1, newArrayList(profile ));
        assertThat(account.getId()).isEqualTo("id");
        assertThat(account.getChargingId()).isEqualTo(chargingId);
        assertThat(account.getProfiles().get(0)).isEqualToComparingFieldByFieldRecursively(profile);
        assertThat(account.getProfiles().size()).isEqualTo(1);
        assertThat(account.getBillingCycleDay()).isEqualTo(1);
        assertThat(account.getCustomerType()).isEqualTo("type");//TODO: add constraint PRE/POST
        assertThat(account.getLastValidate()).isEqualTo(date);

        final EnrichedAccountInfo info = aEnrichedAccountInfo();
        final Account account2 = new Account(chargingId, info, date);
        assertThat(account2.getId()).isNull();
        assertThat(account2.getChargingId()).isEqualTo(chargingId);
        final Profile profile2 = Profile.builder()
                .userGroups(info.getUsergroups())
                .build();
        assertThat(account2.getProfiles().get(0)).isEqualToComparingFieldByFieldRecursively(profile2);
        assertThat(account2.getProfiles().size()).isEqualTo(1);
        assertThat(account2.getBillingCycleDay()).isEqualTo(info.getBillingCycleDay());
        assertThat(account2.getCustomerType()).isEqualTo(info.getCustomerType());
        assertThat(account2.getLastValidate()).isEqualTo(date);
    }

    @Test
    public void shouldGetObjectAsMap() throws Exception {
        final ChargingId chargingId = aChargingId();
        final Date date = aFixedDate();
        final Profile profile = aProfile();
        final Account account = new Account("id", chargingId, date, "type", 1, newArrayList(profile ));
        Map<String, Object> values = account.asMap();
        //check that the key/values are correct

        List<Field> fieldArr = newArrayList(account.getClass().getDeclaredFields());
        Set<String> valuesSet = values.keySet();
        List<Method> methods = newArrayList(ContextData.class.getMethods());

        //check all the fields
        for (Field field : fieldArr) {
            assertThat(valuesSet).contains(field.getName());

            //check all methods are there
            for (Method method : methods) {

                if (method.getName().equals("get" + field)) {
                    System.out.println("Method: " + method.getName());
                    assertThat(values.get(field.getName())).isEqualTo(method.invoke(account));
                    break;
                }
            }
        }
    }

}
