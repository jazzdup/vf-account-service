package com.vodafone.charging.accountservice.domain.enums;


public enum ERIFRequestTarget {

    LOCAL("local"),
    GLOBAL("global");

    ERIFRequestTarget(String value) {
        this.value = value;
    }

    String value;

    public String getValue() {
        return value;
    }
}
