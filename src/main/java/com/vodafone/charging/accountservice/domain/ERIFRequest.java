package com.vodafone.charging.accountservice.domain;

import lombok.ToString;

@ToString
public class ERIFRequest {
    private MessageControl messageControl;
    private Routable routable;

    public ERIFRequest() {
    }

    public ERIFRequest(MessageControl messageControl, Routable routable) {
        this.messageControl = messageControl;
        this.routable = routable;
    }

    public MessageControl getMessageControl() {
        return messageControl;
    }

    public Routable getRoutable() {
        return routable;
    }

}
