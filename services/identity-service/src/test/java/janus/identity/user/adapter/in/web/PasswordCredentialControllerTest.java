package janus.identity.user.adapter.in.web;

import janus.common.config.ApiVersioningConfig;
import janus.identity.user.application.port.usecase.PasswordCredentialUseCase;
import janus.identity.user.testdata.UserTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static janus.identity.user.testdata.UserTestDataFactory.TEST_NEW_PASSWORD;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_PASSWORD;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_USER_ID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PasswordCredentialController.class)
@Import(ApiVersioningConfig.class)
public class PasswordCredentialControllerTest {

    private static final String PASSWORD_ENDPOINT = "/api/v1/users/" + TEST_USER_ID + "/password";
    private static final String PASSWORD_VERIFICATION_ENDPOINT = PASSWORD_ENDPOINT + "/verify";
    private static final String VALID_PATH = "$.valid";

    private static final String PASSWORD_VERIFICATION_REQUEST = """
            {
              "password": "test-password"
            }
            """;

    private static final String PASSWORD_CHANGE_REQUEST = """
            {
              "currentPassword": "test-password",
              "newPassword": "new-test-password"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordCredentialUseCase passwordCredentialUseCaseMock;

    @Test
    void testWhenPasswordMatches_thenReturnTrue() throws Exception {
        when(passwordCredentialUseCaseMock.verifyPassword(TEST_USER_ID, TEST_PASSWORD)).thenReturn(true);

        mockMvc.perform(post(PASSWORD_VERIFICATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PASSWORD_VERIFICATION_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(VALID_PATH).value(true));
    }

    @Test
    void testWhenPasswordChangeValid_thenChangePassword() throws Exception {
        mockMvc.perform(put(PASSWORD_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PASSWORD_CHANGE_REQUEST))
                .andExpect(status().isNoContent());

        verify(passwordCredentialUseCaseMock)
                .changePassword(TEST_USER_ID, TEST_PASSWORD, TEST_NEW_PASSWORD);
    }
}
