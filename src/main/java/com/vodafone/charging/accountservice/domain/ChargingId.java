package com.vodafone.charging.accountservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Component;

@Component
public class ChargingId {
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Type {
        VODAFONE_ID("vodafoneid"),
        MSISDN("msisdn"),
        PSTN("pstn"),
        STB("stb");

        private final String chargingType;

        Type(String type) {
            this.chargingType = type;
        }

        public String type() {
            return chargingType;
        }
    }

    private String type;
    private String value;

    public ChargingId() {
    }

    public ChargingId(final Type type, final String value) {
        this.type = type.type();
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {
        private Type type;
        private String value;

        public ChargingId.Builder type(final Type type) {
            this.type = type;
            return this;
        }
        public ChargingId.Builder value(final String value) {
            this.value = value;
            return this;
        }

        public ChargingId build() {
            return new ChargingId(type, value);
        }
    }

    @Override
    public String toString() {
        return "ChargingId{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
