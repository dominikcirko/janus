package janus.auth.oauth2.client.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Authorization endpoint response types that a client can register to use.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2">RFC 7591, Section 2</a>
 */
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
