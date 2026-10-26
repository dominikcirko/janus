package janus.auth.oauth2.client.testdata;

import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;

import java.util.Collections;
import java.util.Set;

public final class RegisteredClientTestDataFactory {

    public static final String TEST_CLIENT_SECRET_HASH = "test-client-secret-hash";

    private static final String TEST_CLIENT_NAME = "Test Client";
    private static final String TEST_CLIENT_URI = "https://client.example.com";
    private static final String TEST_HTTP_CLIENT_URI = "http://client.example.com";
    private static final String TEST_REDIRECT_URI = "https://client.example.com/callback";
    private static final String TEST_SCOPE = "openid profile";
    private static final String TEST_DESCRIPTION = "Test client description";

    private RegisteredClientTestDataFactory() {
    }

    public static RegisteredClientInput createValidConfidentialInput() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(),
                TEST_HTTP_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createValidConfidentialAuthorizationCodeInput() {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createValidPublicInput() {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.NONE,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                null,
                null
        );
    }

    public static RegisteredClientInput createAuthorizationCodeInputWithoutRedirectUri() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createAuthorizationCodeInputWithoutResponseType() {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createClientCredentialsInputWithCodeResponseType() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createPublicClientCredentialsInput() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.NONE,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createInputWithRedirectUri(String redirectUri) {
        return createInput(
                Collections.singleton(redirectUri),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createInputWithClientUri(String clientUri) {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                clientUri,
                TEST_SCOPE
        );
    }

    public static RegisteredClientInput createInputWithScope(String scope) {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                scope
        );
    }

    private static RegisteredClientInput createInput(
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
