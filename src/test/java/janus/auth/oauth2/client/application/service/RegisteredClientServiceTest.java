package janus.auth.oauth2.client.application.service;

import janus.auth.oauth2.client.adapter.out.persistence.entity.RegisteredClientEntity;
import janus.auth.oauth2.client.application.mapper.RegisteredClientMapper;
import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;
import janus.auth.oauth2.client.domain.exception.RegisteredClientError;
import janus.auth.oauth2.client.domain.exception.RegisteredClientException;
import janus.auth.oauth2.client.testdata.RegisteredClientTestDataFactory;
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

import static janus.auth.oauth2.client.testdata.RegisteredClientTestDataFactory.TEST_CLIENT_SECRET_HASH;
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
        var input = RegisteredClientTestDataFactory.createValidConfidentialInput();
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
        var input = RegisteredClientTestDataFactory.createValidPublicInput();
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
        var input = RegisteredClientTestDataFactory
                .createAuthorizationCodeInputWithoutRedirectUri();

        assertRegistrationRejected(input, RegisteredClientError.INVALID_REDIRECT_URI);
    }

    @Test
    void testWhenAuthorizationCodeResponseTypeMissing_thenRejectRegistration() {
        var input = RegisteredClientTestDataFactory
                .createAuthorizationCodeInputWithoutResponseType();

        assertRegistrationRejected(input, RegisteredClientError.INVALID_CLIENT_METADATA);
    }

    @Test
    void testWhenCodeResponseTypeHasNoAuthorizationCodeGrant_thenRejectRegistration() {
        var input = RegisteredClientTestDataFactory
                .createClientCredentialsInputWithCodeResponseType();

        assertRegistrationRejected(input, RegisteredClientError.INVALID_CLIENT_METADATA);
    }

    @Test
    void testWhenPublicClientUsesClientCredentials_thenRejectRegistration() {
        var input = RegisteredClientTestDataFactory.createPublicClientCredentialsInput();

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
        var input = RegisteredClientTestDataFactory.createInputWithRedirectUri(redirectUri);

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
        var input = RegisteredClientTestDataFactory.createInputWithClientUri(clientUri);

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
        var input = RegisteredClientTestDataFactory.createInputWithScope(scope);

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

}
