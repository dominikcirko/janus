package janus.auth.oauth2.client.domain.type;

public enum ClientStatus {
    ACTIVE("active"),
    DISABLED("disabled");

    private final String value;

    ClientStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
