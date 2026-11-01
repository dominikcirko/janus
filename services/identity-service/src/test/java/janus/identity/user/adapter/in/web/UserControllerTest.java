package janus.identity.user.adapter.in.web;

import janus.common.config.ApiVersioningConfig;
import janus.identity.user.adapter.in.web.mapper.UserWebMapper;
import janus.identity.user.application.port.usecase.UserUseCase;
import janus.identity.user.domain.type.UserStatus;
import janus.identity.user.testdata.UserTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static janus.identity.user.testdata.UserTestDataFactory.TEST_USER_ID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ApiVersioningConfig.class)
public class UserControllerTest {

    private static final String USERS_ENDPOINT = "/api/v1/users";
    private static final String USER_ENDPOINT = USERS_ENDPOINT + "/" + TEST_USER_ID;
    private static final String USER_STATUS_ENDPOINT = USER_ENDPOINT + "/status";

    private static final String ID_PATH = "$.id";
    private static final String STATUS_PATH = "$.status";
    private static final String EMAIL_PATH = "$.email";
    private static final String DISPLAY_NAME_PATH = "$.displayName";
    private static final String FIRST_ID_PATH = "$[0].id";

    private static final String VALID_USER_REQUEST = """
            {
              "email": "user@example.com",
              "password": "test-password",
              "displayName": "Test User",
              "givenName": "Test",
              "familyName": "User",
              "pictureUri": "https://example.com/user.png",
              "userMetadata": {
                "department": "engineering"
              }
            }
            """;

    private static final String VALID_USER_UPDATE_REQUEST = """
            {
              "displayName": "Updated User",
              "givenName": "Updated",
              "familyName": "Name",
              "pictureUri": "https://example.com/updated-user.png",
              "userMetadata": {
                "department": "security"
              }
            }
            """;

    private static final String SUSPENDED_STATUS_REQUEST = """
            {
              "status": "SUSPENDED"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserUseCase userUseCaseMock;

    @MockitoBean
    private UserWebMapper userWebMapperMock;

    @Test
    void testWhenUserRequestValid_thenCreateUser() throws Exception {
        var request = UserTestDataFactory.createValidRequest();
        var input = UserTestDataFactory.createValidInput();
        var output = UserTestDataFactory.createActiveOutput();
        var response = UserTestDataFactory.createActiveResponse();
        when(userWebMapperMock.toCreateInput(request)).thenReturn(input);
        when(userUseCaseMock.createUser(input)).thenReturn(output);
        when(userWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(STATUS_PATH).value(response.status().name()))
                .andExpect(jsonPath(EMAIL_PATH).value(response.email()))
                .andExpect(jsonPath(DISPLAY_NAME_PATH).value(response.displayName()));
    }

    @Test
    void testWhenUserExists_thenReturnUser() throws Exception {
        var output = UserTestDataFactory.createActiveOutput();
        var response = UserTestDataFactory.createActiveResponse();
        when(userUseCaseMock.getUser(TEST_USER_ID)).thenReturn(output);
        when(userWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(get(USER_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(EMAIL_PATH).value(response.email()));
    }

    @Test
    void testWhenUsersExist_thenReturnUsers() throws Exception {
        var output = UserTestDataFactory.createActiveOutput();
        var response = UserTestDataFactory.createActiveResponse();
        when(userUseCaseMock.getUsers()).thenReturn(List.of(output));
        when(userWebMapperMock.toResponse(List.of(output))).thenReturn(List.of(response));

        mockMvc.perform(get(USERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(FIRST_ID_PATH).value(response.id().toString()));
    }

    @Test
    void testWhenUserUpdateValid_thenUpdateUser() throws Exception {
        var request = UserTestDataFactory.createValidUpdateRequest();
        var input = UserTestDataFactory.createValidUpdateInput();
        var output = UserTestDataFactory.createActiveOutput();
        var response = UserTestDataFactory.createActiveResponse();
        when(userWebMapperMock.toUpdateInput(request)).thenReturn(input);
        when(userUseCaseMock.updateUser(TEST_USER_ID, input)).thenReturn(output);
        when(userWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(put(USER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_UPDATE_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(DISPLAY_NAME_PATH).value(response.displayName()));
    }

    @Test
    void testWhenUserStatusValid_thenChangeUserStatus() throws Exception {
        var output = UserTestDataFactory.createSuspendedOutput();
        var response = UserTestDataFactory.createSuspendedResponse();
        when(userUseCaseMock.changeUserStatus(TEST_USER_ID, UserStatus.SUSPENDED)).thenReturn(output);
        when(userWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(patch(USER_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SUSPENDED_STATUS_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(STATUS_PATH).value(UserStatus.SUSPENDED.name()));
    }

    @Test
    void testWhenUserExists_thenDeleteUser() throws Exception {
        mockMvc.perform(delete(USER_ENDPOINT))
                .andExpect(status().isNoContent());

        verify(userUseCaseMock).deleteUser(TEST_USER_ID);
    }
}
