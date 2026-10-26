package janus.auth.oauth2.client.adapter.in.web;

import janus.auth.oauth2.client.adapter.in.web.mapper.RegisteredClientWebMapper;
import janus.auth.oauth2.client.application.port.usecase.RegisteredClientUseCase;
import janus.config.ApiVersioningConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static janus.auth.oauth2.client.testdata.RegisteredClientTestDataFactory.createValidConfidentialAuthorizationCodeInput;
import static janus.auth.oauth2.client.testdata.RegisteredClientTestDataFactory.createValidRegisteredClientOutput;
import static janus.auth.oauth2.client.testdata.RegisteredClientTestDataFactory.createValidRegisteredClientRequest;
import static janus.auth.oauth2.client.testdata.RegisteredClientTestDataFactory.createValidRegisteredClientResponse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisteredClientController.class)
@Import(ApiVersioningConfig.class)
public class RegisteredClientControllerTest {

    private static final String REGISTRATION_ENDPOINT = "/api/v1/clients";
    private static final String NO_STORE_CACHE_CONTROL = "no-store";

    private static final String CLIENT_ID_PATH = "$.client_id";
    private static final String CLIENT_SECRET_PATH = "$.client_secret";
    private static final String CLIENT_ID_ISSUED_AT_PATH = "$.client_id_issued_at";
    private static final String CLIENT_SECRET_EXPIRES_AT_PATH = "$.client_secret_expires_at";
    private static final String FIRST_REDIRECT_URI_PATH = "$.redirect_uris[0]";
    private static final String AUTHENTICATION_METHOD_PATH = "$.token_endpoint_auth_method";
    private static final String FIRST_GRANT_TYPE_PATH = "$.grant_types[0]";
    private static final String FIRST_RESPONSE_TYPE_PATH = "$.response_types[0]";
    private static final String CLIENT_NAME_PATH = "$.client_name";
    private static final String CLIENT_URI_PATH = "$.client_uri";
    private static final String SCOPE_PATH = "$.scope";
    private static final String DESCRIPTION_PATH = "$.description";

    private static final String VALID_REGISTRATION_REQUEST = """
            {
              "redirect_uris": ["https://client.example.com/callback"],
              "token_endpoint_auth_method": "client_secret_basic",
              "grant_types": ["authorization_code"],
              "response_types": ["code"],
              "client_name": "Test Client",
              "client_uri": "https://client.example.com",
              "scope": "openid profile",
              "description": "Test client description"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisteredClientUseCase registeredClientUseCaseMock;

    @MockitoBean
    private RegisteredClientWebMapper registeredClientWebMapperMock;

    @Test
    void testWhenRegistrationRequestValid_thenReturnRegisteredClient() throws Exception {
        var request = createValidRegisteredClientRequest();
        var input = createValidConfidentialAuthorizationCodeInput();
        var output = createValidRegisteredClientOutput();
        var response = createValidRegisteredClientResponse();
        when(registeredClientWebMapperMock.toInput(request)).thenReturn(input);
        when(registeredClientUseCaseMock.registerClient(input)).thenReturn(output);
        when(registeredClientWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REGISTRATION_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, NO_STORE_CACHE_CONTROL))
                .andExpect(jsonPath(CLIENT_ID_PATH).value(response.clientId()))
                .andExpect(jsonPath(CLIENT_SECRET_PATH).value(response.clientSecret()))
                .andExpect(jsonPath(CLIENT_ID_ISSUED_AT_PATH).value(response.clientIdIssuedAt()))
                .andExpect(jsonPath(CLIENT_SECRET_EXPIRES_AT_PATH).value(response.clientSecretExpiresAt()))
                .andExpect(jsonPath(FIRST_REDIRECT_URI_PATH).value(response.redirectUris().iterator().next()))
                .andExpect(jsonPath(AUTHENTICATION_METHOD_PATH).value(response.tokenEndpointAuthMethod().value()))
                .andExpect(jsonPath(FIRST_GRANT_TYPE_PATH).value(response.grantTypes().iterator().next().value()))
                .andExpect(jsonPath(FIRST_RESPONSE_TYPE_PATH).value(response.responseTypes().iterator().next().value()))
                .andExpect(jsonPath(CLIENT_NAME_PATH).value(response.clientName()))
                .andExpect(jsonPath(CLIENT_URI_PATH).value(response.clientUri()))
                .andExpect(jsonPath(SCOPE_PATH).value(response.scope()))
                .andExpect(jsonPath(DESCRIPTION_PATH).value(response.description()));
    }
}
