package janus.auth.oauth2.client.adapter.in.web;

import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientRequest;
import janus.auth.oauth2.client.adapter.in.web.mapper.RegisteredClientWebMapper;
import janus.auth.oauth2.client.application.port.usecase.RegisteredClientUseCase;
import janus.auth.oauth2.client.domain.exception.RegisteredClientError;
import janus.auth.oauth2.client.domain.exception.RegisteredClientException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisteredClientController.class)
@Import({
        ApiVersioningConfig.class,
        RegisteredClientExceptionHandler.class
})
public class RegisteredClientExceptionHandlerTest {

    private static final String REGISTRATION_ENDPOINT = "/api/v1/clients";
    private static final String ERROR_PATH = "$.error";
    private static final String ERROR_DESCRIPTION_PATH = "$.error_description";
    private static final String NO_STORE_CACHE_CONTROL = "no-store";

    private static final String EMPTY_REGISTRATION_REQUEST = "{}";
    private static final String BLANK_REDIRECT_URI_REQUEST = "{\"redirect_uris\":[\"\"]}";
    private static final String MALFORMED_REGISTRATION_REQUEST = "{";

    private static final int MAX_CLIENT_NAME_LENGTH = 255;
    private static final String TOO_LONG_CLIENT_NAME = "a".repeat(MAX_CLIENT_NAME_LENGTH + 1);
    private static final String TOO_LONG_CLIENT_NAME_REQUEST = "{\"client_name\":\"" + TOO_LONG_CLIENT_NAME + "\"}";

    private static final String REGISTRATION_REJECTION_MESSAGE = "Registration metadata is not accepted";
    private static final String INVALID_REDIRECT_URI_MESSAGE = "One or more redirect URIs are invalid";
    private static final String INVALID_CLIENT_METADATA_MESSAGE = "One or more client metadata fields are invalid";
    private static final String MALFORMED_REQUEST_MESSAGE =
            "Registration request contains malformed JSON or invalid metadata values";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisteredClientUseCase registeredClientUseCaseMock;

    @MockitoBean
    private RegisteredClientWebMapper registeredClientWebMapperMock;

    @Test
    void testWhenRegistrationRejected_thenReturnRegistrationError() throws Exception {
        var input = createValidConfidentialAuthorizationCodeInput();
        var exception = new RegisteredClientException(
                RegisteredClientError.INVALID_CLIENT_METADATA,
                REGISTRATION_REJECTION_MESSAGE
        );
        when(registeredClientWebMapperMock.toInput(any(RegisteredClientRequest.class)))
                .thenReturn(input);
        when(registeredClientUseCaseMock.registerClient(input)).thenThrow(exception);

        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EMPTY_REGISTRATION_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, NO_STORE_CACHE_CONTROL))
                .andExpect(jsonPath(ERROR_PATH)
                        .value(RegisteredClientError.INVALID_CLIENT_METADATA.value()))
                .andExpect(jsonPath(ERROR_DESCRIPTION_PATH)
                        .value(REGISTRATION_REJECTION_MESSAGE));
    }

    @Test
    void testWhenRedirectUriInvalid_thenReturnInvalidRedirectUriError() throws Exception {
        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_REDIRECT_URI_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, NO_STORE_CACHE_CONTROL))
                .andExpect(jsonPath(ERROR_PATH)
                        .value(RegisteredClientError.INVALID_REDIRECT_URI.value()))
                .andExpect(jsonPath(ERROR_DESCRIPTION_PATH)
                        .value(INVALID_REDIRECT_URI_MESSAGE));
    }

    @Test
    void testWhenClientMetadataInvalid_thenReturnInvalidClientMetadataError() throws Exception {
        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TOO_LONG_CLIENT_NAME_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, NO_STORE_CACHE_CONTROL))
                .andExpect(jsonPath(ERROR_PATH)
                        .value(RegisteredClientError.INVALID_CLIENT_METADATA.value()))
                .andExpect(jsonPath(ERROR_DESCRIPTION_PATH)
                        .value(INVALID_CLIENT_METADATA_MESSAGE));
    }

    @Test
    void testWhenRequestBodyMalformed_thenReturnInvalidClientMetadataError() throws Exception {
        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MALFORMED_REGISTRATION_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, NO_STORE_CACHE_CONTROL))
                .andExpect(jsonPath(ERROR_PATH)
                        .value(RegisteredClientError.INVALID_CLIENT_METADATA.value()))
                .andExpect(jsonPath(ERROR_DESCRIPTION_PATH)
                        .value(MALFORMED_REQUEST_MESSAGE));
    }
}
