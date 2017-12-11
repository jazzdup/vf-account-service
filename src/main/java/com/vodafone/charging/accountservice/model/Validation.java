package com.vodafone.charging.accountservice.model;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Validation {

    private List<String> usergroups;

    public List<String> getUsergroups() {
        return usergroups;
    }

    public Validation() {

    }

}
