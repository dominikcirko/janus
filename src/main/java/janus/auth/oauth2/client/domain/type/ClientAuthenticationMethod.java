package janus.auth.oauth2.client.domain.type;

public enum ClientAuthenticationMethod {
    NONE("none"),
    CLIENT_SECRET_BASIC("client_secret_basic");

    private final String value;

    ClientAuthenticationMethod(String value) {
        this.value = value;
    }
    public String value() {
        return value;
    }
}
