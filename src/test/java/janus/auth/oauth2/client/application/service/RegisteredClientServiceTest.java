package janus.auth.oauth2.client.application.service;

import janus.auth.oauth2.client.adapter.out.persistence.entity.RegisteredClientEntity;
import janus.auth.oauth2.client.application.mapper.RegisteredClientMapper;
import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;
import janus.auth.oauth2.client.domain.exception.RegisteredClientError;
import janus.auth.oauth2.client.domain.exception.RegisteredClientException;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisteredClientServiceTest {

    private static final String TEST_CLIENT_NAME = "Test Client";
    private static final String TEST_CLIENT_URI = "https://client.example.com";
    private static final String TEST_HTTP_CLIENT_URI = "http://client.example.com";
    private static final String TEST_REDIRECT_URI = "https://client.example.com/callback";
    private static final String TEST_SCOPE = "openid profile";
    private static final String TEST_DESCRIPTION = "Test client description";
    private static final String TEST_CLIENT_SECRET_HASH = "test-client-secret-hash";

    @Mock
    private RegisteredClientRepository registeredClientRepositoryMock;
    @Mock
    private RegisteredClientMapper registeredClientMapperMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private RegisteredClientEntity mappedClientMock;
    @Mock
    private RegisteredClientEntity savedClientMock;
    @Mock
    private RegisteredClientOutput outputMock;

    private RegisteredClientService registeredClientService;

    @BeforeEach
    void setUp() {
        registeredClientService = new RegisteredClientService(
                registeredClientRepositoryMock,
                registeredClientMapperMock,
                passwordEncoderMock
        );
    }

    @Test
    void testWhenConfidentialRegistrationValid_thenGenerateSecretHashAndSaveClient() {
        var input = createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(),
                TEST_HTTP_CLIENT_URI,
                TEST_SCOPE
        );
        when(passwordEncoderMock.encode(anyString())).thenReturn(TEST_CLIENT_SECRET_HASH);
        when(registeredClientMapperMock.toEntity(
                same(input),
                anyString(),
                eq(TEST_CLIENT_SECRET_HASH)
        )).thenReturn(mappedClientMock);
        when(registeredClientRepositoryMock.save(mappedClientMock)).thenReturn(savedClientMock);
        when(registeredClientMapperMock.toOutput(
                same(savedClientMock),
                anyString()
        )).thenReturn(outputMock);

        var result = registeredClientService.registerClient(input);

        var clientSecretCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoderMock).encode(clientSecretCaptor.capture());
        verify(registeredClientMapperMock).toOutput(
                same(savedClientMock),
                eq(clientSecretCaptor.getValue())
        );
        assertEquals(32,
                Base64.getUrlDecoder().decode(clientSecretCaptor.getValue()).length);
        assertSame(outputMock, result);
    }

    @Test
    void testWhenPublicRegistrationValid_thenSaveClientWithoutSecret() {
        var input = createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.NONE,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                null,
                null
        );
        when(registeredClientMapperMock.toEntity(
                same(input),
                anyString(),
                isNull()
        )).thenReturn(mappedClientMock);
        when(registeredClientRepositoryMock.save(mappedClientMock)).thenReturn(savedClientMock);
        when(registeredClientMapperMock.toOutput(savedClientMock, null)).thenReturn(outputMock);

        var result = registeredClientService.registerClient(input);

        verifyNoInteractions(passwordEncoderMock);
        verify(registeredClientMapperMock).toOutput(savedClientMock, null);
        assertSame(outputMock, result);
    }

    @Test
    void testWhenAuthorizationCodeRedirectUriMissing_thenRejectRegistration() {
        var input = createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );

        assertRegistrationRejected(input, RegisteredClientError.INVALID_REDIRECT_URI);
    }

    @Test
    void testWhenAuthorizationCodeResponseTypeMissing_thenRejectRegistration() {
        var input = createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );

        assertRegistrationRejected(input, RegisteredClientError.INVALID_CLIENT_METADATA);
    }

    @Test
    void testWhenCodeResponseTypeHasNoAuthorizationCodeGrant_thenRejectRegistration() {
        var input = createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );

        assertRegistrationRejected(input, RegisteredClientError.INVALID_CLIENT_METADATA);
    }

    @Test
    void testWhenPublicClientUsesClientCredentials_thenRejectRegistration() {
        var input = createInput(
                Set.of(),
                ClientAuthenticationMethod.NONE,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );

        assertRegistrationRejected(input, RegisteredClientError.INVALID_CLIENT_METADATA);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "callback",
            "https://client.example.com/callback#fragment",
            "https://client example.com/callback"
    })
    void testWhenRedirectUriInvalid_thenRejectRegistration(String redirectUri) {
        var input = createInput(
                Collections.singleton(redirectUri),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );

        assertRegistrationRejected(input, RegisteredClientError.INVALID_REDIRECT_URI);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "ftp://client.example.com",
            "https:/client",
            "https://client example.com"
    })
    void testWhenClientUriInvalid_thenRejectRegistration(String clientUri) {
        var input = createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                clientUri,
                TEST_SCOPE
        );

        assertRegistrationRejected(input, RegisteredClientError.INVALID_CLIENT_METADATA);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "openid  profile",
            "openid\tprofile",
            "open\"id"
    })
    void testWhenScopeInvalid_thenRejectRegistration(String scope) {
        var input = createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                scope
        );

        assertRegistrationRejected(input, RegisteredClientError.INVALID_CLIENT_METADATA);
    }

    private void assertRegistrationRejected(RegisteredClientInput input,
                                            RegisteredClientError expectedError) {
        var exception = assertThrows(
                RegisteredClientException.class,
                () -> registeredClientService.registerClient(input));

        assertEquals(expectedError.value(), exception.getErrorCode());
        verifyNoInteractions(
                registeredClientRepositoryMock,
                registeredClientMapperMock,
                passwordEncoderMock
        );
    }

    private RegisteredClientInput createInput(
            Set<String> redirectUris,
            ClientAuthenticationMethod authenticationMethod,
            Set<GrantType> grantTypes,
            Set<ResponseType> responseTypes,
            String clientUri,
            String scope
    ) {
        return new RegisteredClientInput(
                redirectUris,
                authenticationMethod,
                grantTypes,
                responseTypes,
                TEST_CLIENT_NAME,
                clientUri,
                scope,
                TEST_DESCRIPTION
        );
    }
}
