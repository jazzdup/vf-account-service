package com.vodafone.charging.accountservice.domain.model;

import com.vodafone.charging.accountservice.domain.ChargingId;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Builder
@Getter @ToString
public class Account {
    @Id
    private String id;
    private ChargingId chargingId;
    private Date lastValidate;
    private String customerType;//PRE/POST
    private List<Profile> profiles;
}
