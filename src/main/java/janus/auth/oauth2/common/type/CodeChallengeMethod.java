package janus.auth.oauth2.common.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * PKCE code-challenge methods supported by Janus.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7636#section-4.2">RFC 7636, Section 4.2</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9700#section-2.1.1">RFC 9700, Section 2.1.1</a>
 */
public enum CodeChallengeMethod {
    S256("S256");

    private final String value;

    CodeChallengeMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
