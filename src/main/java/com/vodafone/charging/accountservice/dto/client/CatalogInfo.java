package com.vodafone.charging.accountservice.dto.client;

import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Builder
@Getter
@ToString
public class CatalogInfo {

    @Qualifier("spendLimitInfo")
    @Autowired
    private List<SpendLimitInfo> defaultSpendLimitInfo;
}
