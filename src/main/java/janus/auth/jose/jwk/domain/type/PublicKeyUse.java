package janus.auth.jose.jwk.domain.type;

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

}
