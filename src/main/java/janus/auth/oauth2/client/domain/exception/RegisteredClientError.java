package janus.auth.oauth2.client.domain.exception;

/**
 * Registration error codes currently supported by Janus.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.2">RFC 7591, Section 3.2.2</a>
 */
public enum RegisteredClientError {
    INVALID_REDIRECT_URI("invalid_redirect_uri"),
    INVALID_CLIENT_METADATA("invalid_client_metadata");

    private final String value;

    RegisteredClientError(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
