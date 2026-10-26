package janus.auth.oauth2.client.application.mapper;

import janus.auth.oauth2.client.adapter.out.persistence.entity.RegisteredClientEntity;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.ClientStatus;
import janus.auth.oauth2.client.domain.type.ClientType;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegisteredClientMapperTest {

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";
    private static final String TEST_CLIENT_SECRET_HASH = "test-client-secret-hash";
    private static final String TEST_CLIENT_NAME = "Test Client";
    private static final String TEST_CLIENT_URI = "https://client.example.com";
    private static final String TEST_REDIRECT_URI = "https://client.example.com/callback";
    private static final String TEST_DESCRIPTION = "Test client description";
    private static final Instant TEST_CLIENT_ID_ISSUED_AT = Instant.parse("2026-10-26T10:00:00Z");
    private static final Instant TEST_CLIENT_SECRET_EXPIRES_AT = Instant.parse("2027-10-26T10:00:00Z");

    private final RegisteredClientMapper mapper =
            Mappers.getMapper(RegisteredClientMapper.class);

    @Test
    void testWhenConfidentialRegistrationInputValid_thenReturnMappedEntity() {
        var input = new RegisteredClientInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                "openid profile",
                TEST_DESCRIPTION
        );

        var result = mapper.toEntity(input, TEST_CLIENT_ID, TEST_CLIENT_SECRET_HASH);

        assertEquals(ClientType.CONFIDENTIAL, result.getType());
        assertEquals(input.redirectUris(), result.getRedirectUris());
        assertEquals(TEST_CLIENT_NAME, result.getClientName());
        assertEquals(TEST_CLIENT_ID, result.getClientId());
        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                result.getAuthenticationMethod());
        assertEquals(ClientStatus.ACTIVE, result.getStatus());
        assertEquals(TEST_CLIENT_SECRET_HASH, result.getClientSecretHash());
        assertEquals(input.grantTypes(), result.getGrantTypes());
        assertEquals(Set.of("openid", "profile"), result.getScopes());
        assertEquals(input.responseTypes(), result.getResponseTypes());
        assertEquals(TEST_CLIENT_URI, result.getClientUri());
        assertEquals(TEST_DESCRIPTION, result.getDescription());
    }

    @Test
    void testWhenPublicRegistrationInputValid_thenReturnMappedEntityWithoutSecretOrScopes() {
        var input = new RegisteredClientInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.NONE,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                null,
                TEST_DESCRIPTION
        );

        var result = mapper.toEntity(input, TEST_CLIENT_ID, null);

        assertEquals(ClientType.PUBLIC, result.getType());
        assertEquals(Set.of(), result.getScopes());
        assertNull(result.getClientSecretHash());
    }

    @Test
    void testWhenRegisteredClientHasExpiringSecret_thenReturnMappedOutput() {
        var entity = RegisteredClientEntity.builder()
                .createdAt(TEST_CLIENT_ID_ISSUED_AT)
                .type(ClientType.CONFIDENTIAL)
                .redirectUris(Set.of(TEST_REDIRECT_URI))
                .clientName(TEST_CLIENT_NAME)
                .clientId(TEST_CLIENT_ID)
                .authenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientSecretExpiresAt(TEST_CLIENT_SECRET_EXPIRES_AT)
                .grantTypes(Set.of(GrantType.AUTHORIZATION_CODE))
                .scopes(Set.of("profile", "openid"))
                .responseTypes(Set.of(ResponseType.CODE))
                .clientUri(TEST_CLIENT_URI)
                .description(TEST_DESCRIPTION)
                .build();

        var result = mapper.toOutput(entity, TEST_CLIENT_SECRET);

        assertEquals(TEST_CLIENT_ID, result.clientId());
        assertEquals(TEST_CLIENT_SECRET, result.clientSecret());
        assertEquals(TEST_CLIENT_ID_ISSUED_AT.getEpochSecond(), result.clientIdIssuedAt());
        assertEquals(TEST_CLIENT_SECRET_EXPIRES_AT.getEpochSecond(),
                result.clientSecretExpiresAt());
        assertEquals(Set.of(TEST_REDIRECT_URI), result.redirectUris());
        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                result.tokenEndpointAuthMethod());
        assertEquals(Set.of(GrantType.AUTHORIZATION_CODE), result.grantTypes());
        assertEquals(Set.of(ResponseType.CODE), result.responseTypes());
        assertEquals(TEST_CLIENT_NAME, result.clientName());
        assertEquals(TEST_CLIENT_URI, result.clientUri());
        assertEquals("openid profile", result.scope());
        assertEquals(TEST_DESCRIPTION, result.description());
    }

    @Test
    void testWhenClientSecretHasNoExpiration_thenReturnZeroExpiration() {
        var entity = RegisteredClientEntity.builder()
                .createdAt(TEST_CLIENT_ID_ISSUED_AT)
                .build();

        var result = mapper.toOutput(entity, TEST_CLIENT_SECRET);

        assertEquals(0L, result.clientSecretExpiresAt());
        assertNull(result.scope());
    }

    @Test
    void testWhenClientHasNoSecret_thenReturnNullExpiration() {
        var entity = RegisteredClientEntity.builder()
                .createdAt(TEST_CLIENT_ID_ISSUED_AT)
                .clientSecretExpiresAt(TEST_CLIENT_SECRET_EXPIRES_AT)
                .build();

        var result = mapper.toOutput(entity, null);

        assertNull(result.clientSecret());
        assertNull(result.clientSecretExpiresAt());
    }
}
