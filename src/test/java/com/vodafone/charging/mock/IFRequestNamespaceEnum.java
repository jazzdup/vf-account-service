package com.vodafone.charging.mock;

public enum IFRequestNamespaceEnum {
    SOAP_NS("soap", "http://www.w3.org/2001/12/soap-envelope"),
    VODAFONE_NS("vf", "http://www.vizzavi.net/chargingandpayments/message/1.0");

    private String prefix;
    private String url;

    IFRequestNamespaceEnum(String prefix, String url) {
        this.prefix = prefix;
        this.url = url;
    }

    public String prefix() {
        return prefix;
    }

    public String url() {
        return url;
    }
}
