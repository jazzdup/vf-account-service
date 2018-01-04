package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import org.springframework.stereotype.Service;

/**
 * A service to store Account information.  The only class to call the Repository itself
 *
 */

@Service
public class CacheService {

    public void saveSummaryAccountInfo(EnrichedAccountInfo enrichedAccountInfo) {
        throw new UnsupportedOperationException();
    }

    public void retrieveSummaryAccountInfo() {
        throw new UnsupportedOperationException();
    }

}
