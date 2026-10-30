package janus.auth.oauth2.client.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Methods a client can use to authenticate at the token endpoint.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-4.2.2">RFC 7591, Section 4.2.2</a>
 */
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
