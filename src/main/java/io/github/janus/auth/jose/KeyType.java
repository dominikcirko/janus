package io.github.janus.auth.jose;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KeyType {
    SYMMETRIC("symmetric"),
    ASYMMETRIC("asymmetric");

    private final String value;

    @JsonValue
    public String getValue(){
        return this.value;
    }

    @JsonCreator
    public static KeyType fromValue(String value) {
        for (KeyType keyType : values()) {
            if (keyType.value.equals(value)) {
                return keyType;
            }
        }
        //todo: implement exception handling
        throw new IllegalArgumentException("Invalid key type: " + value);
    }


}
