package janus.auth.oauth2.client.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * OAuth grant types that a client can register to use.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2">RFC 7591, Section 2</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8628#section-3.4">RFC 8628, Section 3.4</a>
 */
public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    DEVICE_CODE("urn:ietf:params:oauth:grant-type:device_code");

    private final String value;

    GrantType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
