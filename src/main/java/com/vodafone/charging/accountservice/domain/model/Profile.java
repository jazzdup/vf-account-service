package com.vodafone.charging.accountservice.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Profile {

    private String accountId;
    private List<String> userGroups;

}
