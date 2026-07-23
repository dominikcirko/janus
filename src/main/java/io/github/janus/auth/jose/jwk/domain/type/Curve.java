package io.github.janus.auth.jose.jwk.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Curve {

    P256("P-256"),
    P384("P-384"),
    P521("P-521");

    private final String value;

    @JsonValue
    public String getValue(){
        return this.value;
    }

    @JsonCreator
    public static Curve fromValue(String value) {
        for (Curve curve : values()) {
            if (curve.value.equals(value)) {
                return curve;
            }
        }
        //todo: implement exception handling
        throw new IllegalArgumentException("Invalid Curve parameter: " + value);
    }
}
