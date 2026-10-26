package janus.auth.oauth2.client.adapter.in.web.mapper;

import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientRequest;
import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientResponse;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisteredClientWebMapperTest {

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";
    private static final String TEST_CLIENT_NAME = "Test Client";
    private static final String TEST_CLIENT_URI = "https://client.example.com";
    private static final String TEST_REDIRECT_URI = "https://client.example.com/callback";
    private static final String TEST_SCOPE = "openid profile";
    private static final String TEST_DESCRIPTION = "Test client description";
    private static final Long TEST_CLIENT_ID_ISSUED_AT = 1_700_000_000L;
    private static final Long TEST_CLIENT_SECRET_EXPIRES_AT = 0L;

    private final RegisteredClientWebMapper mapper =
            Mappers.getMapper(RegisteredClientWebMapper.class);

    @Test
    void testWhenRequestValid_thenReturnInput() {
        var request = new RegisteredClientRequest(
                Set.of(TEST_REDIRECT_URI),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                Set.of(GrantType.AUTHORIZATION_CODE),
                Set.of(ResponseType.CODE),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                TEST_SCOPE,
                TEST_DESCRIPTION
        );
        var expected = new RegisteredClientInput(
                request.redirectUris(),
                request.tokenEndpointAuthMethod(),
                request.grantTypes(),
                request.responseTypes(),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                TEST_SCOPE,
                TEST_DESCRIPTION
        );

        var result = mapper.toInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOutputValid_thenReturnResponse() {
        var output = new RegisteredClientOutput(
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
        var expected = new RegisteredClientResponse(
                TEST_CLIENT_ID,
                TEST_CLIENT_SECRET,
                TEST_CLIENT_ID_ISSUED_AT,
                TEST_CLIENT_SECRET_EXPIRES_AT,
                output.redirectUris(),
                output.tokenEndpointAuthMethod(),
                output.grantTypes(),
                output.responseTypes(),
                TEST_CLIENT_NAME,
                TEST_CLIENT_URI,
                TEST_SCOPE,
                TEST_DESCRIPTION
        );

        var result = mapper.toResponse(output);

        assertEquals(expected, result);
    }
}
