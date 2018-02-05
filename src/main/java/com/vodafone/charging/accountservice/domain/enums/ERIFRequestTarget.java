package com.vodafone.charging.accountservice.domain.enums;


public enum ERIFRequestTarget {

    GLOBAL("global"),
    LOCAL("local");

    ERIFRequestTarget(String value) {
        this.value = value;
    }

    String value;

}
