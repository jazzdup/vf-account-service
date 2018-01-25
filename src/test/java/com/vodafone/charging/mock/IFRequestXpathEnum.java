package com.vodafone.charging.mock;


public enum IFRequestXpathEnum {
    VALIDATE("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:validate"),
    PAYMENT_AUTH("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:paymentAuth"),
    PAYMENT_CAPTURE("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:paymentCapture"),
    NOTIFICATION("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:notification"),
    PAYMENT_REFUND("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:paymentRefund"),
    PROVISION("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:provision"),
    PAYMENT_CANCEL("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:paymentCancelAuth"),
    PAYMENT_CAPTURE_AND_NOTIFICATION(PAYMENT_CAPTURE.path() + " | " + NOTIFICATION.path()),
    REMAINING_BALANCE("//soap:Envelope/soap:Body/vf:messagegroup/vf:request/vf:remainingBalance"),
    SLASH("/"),
    COLON(":");

    private String path;

    IFRequestXpathEnum(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
