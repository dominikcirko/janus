package janus.auth.oauth2.client.adapter.in.web.dto;

import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisteredClientRequestTest {

    private static final String EMPTY_REQUEST = "{}";
    private static final String TEST_REDIRECT_URI = "https://client.example.com/callback";

    @Test
    void testWhenProtocolMetadataOmitted_thenApplyDefaults() throws JacksonException {
        var request = new ObjectMapper().readValue(EMPTY_REQUEST, RegisteredClientRequest.class);

        assertEquals(Set.of(), request.redirectUris());
        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, request.tokenEndpointAuthMethod());
        assertEquals(Set.of(GrantType.AUTHORIZATION_CODE), request.grantTypes());
        assertEquals(Set.of(ResponseType.CODE), request.responseTypes());
    }

    @Test
    void testWhenMutableSetsProvided_thenStoreDefensiveCopies() {
        var redirectUris = new HashSet<>(Set.of(TEST_REDIRECT_URI));
        var grantTypes = new HashSet<>(Set.of(GrantType.AUTHORIZATION_CODE));
        var responseTypes = new HashSet<>(Set.of(ResponseType.CODE));
        var request = new RegisteredClientRequest(
                redirectUris,
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                grantTypes,
                responseTypes,
                null,
                null,
                null,
                null
        );

        redirectUris.clear();
        grantTypes.clear();
        responseTypes.clear();

        assertEquals(Set.of(TEST_REDIRECT_URI), request.redirectUris());
        assertEquals(Set.of(GrantType.AUTHORIZATION_CODE), request.grantTypes());
        assertEquals(Set.of(ResponseType.CODE), request.responseTypes());
    }
}
