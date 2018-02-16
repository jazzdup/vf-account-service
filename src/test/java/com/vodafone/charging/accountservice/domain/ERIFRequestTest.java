package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.dto.json.ERIFRequest;
import com.vodafone.charging.accountservice.dto.json.MessageControl;
import com.vodafone.charging.accountservice.dto.json.Routable;
import org.junit.Test;

import java.util.Locale;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ERIFRequestTest {

    @Test
    public void shouldCreateERIFRequest() {
        ContextData contextData = aContextData();
        MessageControl messageControl = new MessageControl(Locale.UK);
        Routable routable = new Routable(RoutableType.validate, contextData);
        ERIFRequest erifRequest = new ERIFRequest(messageControl, routable);

        assertThat(erifRequest.getMessageControl()).isEqualTo(messageControl);
        assertThat(erifRequest.getRoutable()).isEqualTo(routable);
    }

    @Test
    public void shouldNotAllowNullMessageControl() {
        ContextData contextData = aContextData();
        MessageControl messageControl = null;
        Routable routable = new Routable(RoutableType.validate, contextData);
        assertThatThrownBy(() -> new ERIFRequest(messageControl, routable))
        .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldNotAllowNullRoutable() {
        MessageControl messageControl = new MessageControl(Locale.UK);
        Routable routable = null;
        assertThatThrownBy(() -> new ERIFRequest(messageControl, routable))
        .isInstanceOf(IllegalArgumentException.class);
    }
}
