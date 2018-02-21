package com.vodafone.charging.accountservice.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Getter @ToString
public class PaymentValidation {
    boolean success;
}
