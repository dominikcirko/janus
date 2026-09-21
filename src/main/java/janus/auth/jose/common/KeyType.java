package janus.auth.jose.common;

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

}
