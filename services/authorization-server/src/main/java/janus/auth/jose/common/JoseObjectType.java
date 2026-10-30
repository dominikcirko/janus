package janus.auth.jose.common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JoseObjectType {

    JWT("JWT"),
    JWT_ACCESS_TOKEN("at+jwt"),
    DPOP_JWT("dpop+jwt");

    @JsonValue
    private final String value;
}
