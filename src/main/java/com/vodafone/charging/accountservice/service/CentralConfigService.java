package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CentralConfigService {
    private final PropertiesAccessor propertiesAccessor;

    public CentralConfigService(PropertiesAccessor propertiesAccessor) {
        this.propertiesAccessor = propertiesAccessor;
    }
}
