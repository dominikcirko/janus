package janus.auth.oauth2.client.domain.type;

public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    DEVICE_CODE("urn:ietf:params:oauth:grant-type:device_code");

    private final String value;

    GrantType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
