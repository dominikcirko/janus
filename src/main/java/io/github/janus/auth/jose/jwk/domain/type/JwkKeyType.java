package io.github.janus.auth.jose.jwk.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JwkKeyType {

    RSA("RSA"),
    EC("EC"),
    OCT("oct");

    private final String value;

    @JsonValue
    public String getValue(){
        return this.value;
    }

    @JsonCreator
    public static JwkKeyType fromValue(String value) {
        for (JwkKeyType keyType : values()) {
            if (keyType.value.equals(value)) {
                return keyType;
            }
        }
        //todo: implement exception handling
        throw new IllegalArgumentException("Invalid key type: " + value);
    }
}
