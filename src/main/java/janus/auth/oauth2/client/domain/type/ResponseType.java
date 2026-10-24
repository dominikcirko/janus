package janus.auth.oauth2.client.domain.type;

public enum ResponseType {
    CODE("code");

    private final String value;

    ResponseType(String value){
        this.value = value;
    }

    public String value() {
        return value;
    }

}
