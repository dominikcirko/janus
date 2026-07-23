package io.github.janus.auth.jose.jwk.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

//https://datatracker.ietf.org/doc/html/rfc7517#page-4
@RequiredArgsConstructor
public enum PublicKeyUse {

    ENC("enc"),
    SIG("sig");

    private final String value;

    @JsonValue
    public String getValue(){
        return this.value;
    }

    @JsonCreator
    public static PublicKeyUse fromValue(String value) {
        for (PublicKeyUse pku : values()) {
            if (pku.value.equals(value)) {
                return pku;
            }
        }
        //todo: implement exception handling
        throw new IllegalArgumentException("Invalid intended use of the public key: " + value);
    }

}
