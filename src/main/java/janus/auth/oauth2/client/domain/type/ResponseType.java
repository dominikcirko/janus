package janus.auth.oauth2.client.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseType {
    CODE("code");

    private final String value;

    ResponseType(String value){
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

}
