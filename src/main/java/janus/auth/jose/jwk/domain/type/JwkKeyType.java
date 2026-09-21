package janus.auth.jose.jwk.domain.type;

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

}
