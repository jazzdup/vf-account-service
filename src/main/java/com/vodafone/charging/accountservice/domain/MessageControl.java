package com.vodafone.charging.accountservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
//@XmlRootElement(name="msgcontrol")
@Getter @Setter @ToString @NoArgsConstructor
public class MessageControl{
    private Locale locale	= null;
//    private Map<String, String> headers;
}
