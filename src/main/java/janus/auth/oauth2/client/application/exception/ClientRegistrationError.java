package janus.auth.oauth2.client.application.exception;

public enum ClientRegistrationError {
    INVALID_REDIRECT_URI("invalid_redirect_uri"),
    INVALID_CLIENT_METADATA("invalid_client_metadata");

    private final String value;

    ClientRegistrationError(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
