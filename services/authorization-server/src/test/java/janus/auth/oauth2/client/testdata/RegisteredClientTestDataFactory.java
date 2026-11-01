package janus.auth.oauth2.client.testdata;

import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientRequest;
import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientResponse;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientCreateInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.common.type.GrantType;
import janus.auth.oauth2.common.type.ResponseType;

import java.util.Collections;
import java.util.Set;

public final class RegisteredClientTestDataFactory {

    public static final String TEST_CLIENT_SECRET_HASH = "test-client-secret-hash";

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";
    private static final String TEST_CLIENT_NAME = "Test Client";
    private static final String TEST_CLIENT_URI = "https://client.example.com";
    private static final String TEST_HTTP_CLIENT_URI = "http://client.example.com";
    private static final String TEST_REDIRECT_URI = "https://client.example.com/callback";
    private static final String TEST_SCOPE = "openid profile";
    private static final String TEST_DESCRIPTION = "Test client description";
    private static final Long TEST_CLIENT_ID_ISSUED_AT = 1_700_000_000L;
    private static final Long TEST_CLIENT_SECRET_EXPIRES_AT = 0L;

    private RegisteredClientTestDataFactory() {
    }

    public static RegisteredClientCreateInput createValidConfidentialInput() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(),
                TEST_HTTP_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createValidConfidentialAuthorizationCodeInput() {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createValidPublicInput() {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.NONE,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                null,
                null
        );
    }

    public static RegisteredClientRequest createValidRegisteredClientRequest() {
        return new RegisteredClientRequest(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                TEST_SCOPE,
                TEST_DESCRIPTION
        );
    }

    public static RegisteredClientOutput createValidRegisteredClientOutput() {
        return new RegisteredClientOutput(
                TEST_CLIENT_ID,
                TEST_CLIENT_SECRET,
                TEST_CLIENT_ID_ISSUED_AT,
                TEST_CLIENT_SECRET_EXPIRES_AT,
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                TEST_SCOPE,
                TEST_DESCRIPTION
        );
    }

    public static RegisteredClientResponse createValidRegisteredClientResponse() {
        return new RegisteredClientResponse(
                TEST_CLIENT_ID,
                TEST_CLIENT_SECRET,
                TEST_CLIENT_ID_ISSUED_AT,
                TEST_CLIENT_SECRET_EXPIRES_AT,
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                TEST_SCOPE,
                TEST_DESCRIPTION
        );
    }

    public static RegisteredClientCreateInput createAuthorizationCodeInputWithoutRedirectUri() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createAuthorizationCodeInputWithoutResponseType() {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createClientCredentialsInputWithCodeResponseType() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createPublicClientCredentialsInput() {
        return createInput(
                Set.of(),
                ClientAuthenticationMethod.NONE,
                Set.of(GrantType.CLIENT_CREDENTIALS),
                Set.of(),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createInputWithRedirectUri(String redirectUri) {
        return createInput(
                Collections.singleton(redirectUri),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createInputWithClientUri(String clientUri) {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                clientUri,
                TEST_SCOPE
        );
    }

    public static RegisteredClientCreateInput createInputWithScope(String scope) {
        return createInput(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_URI,
                scope
        );
    }

    private static RegisteredClientCreateInput createInput(
            Set<String> redirectUris,
            ClientAuthenticationMethod authenticationMethod,
            Set<GrantType> grantTypes,
            Set<ResponseType> responseTypes,
            String clientUri,
            String scope
    ) {
        return new RegisteredClientCreateInput(
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
