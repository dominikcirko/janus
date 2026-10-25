package janus.auth.oauth2.client.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClientAuthenticationMethod {
    NONE("none"),
    CLIENT_SECRET_BASIC("client_secret_basic");

    private final String value;

    ClientAuthenticationMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
