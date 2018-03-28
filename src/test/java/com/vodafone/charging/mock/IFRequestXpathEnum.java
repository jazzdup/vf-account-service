package com.vodafone.charging.mock;


public enum IFRequestXpathEnum {
    VALIDATE("//SOAP-ENV:Envelope/SOAP-ENV:Body/ns2:messagegroup/ns2:request/ns2:validate");

    private String path;

    IFRequestXpathEnum(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
