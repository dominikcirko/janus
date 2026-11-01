package janus.tenancy.organization.adapter.in.web;

import janus.common.config.ApiVersioningConfig;
import janus.tenancy.organization.adapter.in.web.mapper.OrganizationMembershipWebMapper;
import janus.tenancy.organization.application.port.usecase.OrganizationMembershipUseCase;
import janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory.TEST_MEMBERSHIP_ID;
import static janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory.TEST_USER_ID;
import static janus.tenancy.organization.testdata.OrganizationTestDataFactory.TEST_ORGANIZATION_ID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationMembershipController.class)
@Import(ApiVersioningConfig.class)
public class OrganizationMembershipControllerTest {

    private static final String ORGANIZATION_MEMBERSHIPS_ENDPOINT =
            "/api/v1/organizations/" + TEST_ORGANIZATION_ID + "/memberships";
    private static final String ORGANIZATION_MEMBERSHIP_ENDPOINT =
            "/api/v1/organization-memberships/" + TEST_MEMBERSHIP_ID;

    private static final String ID_PATH = "$.id";
    private static final String ORGANIZATION_ID_PATH = "$.organizationId";
    private static final String USER_ID_PATH = "$.userId";
    private static final String ROLE_PATH = "$.role";
    private static final String FIRST_ID_PATH = "$[0].id";

    private static final String VALID_ORGANIZATION_MEMBERSHIP_REQUEST = """
            {
              "userId": "%s",
              "role": "ADMIN"
            }
            """.formatted(TEST_USER_ID);

    private static final String VALID_ORGANIZATION_MEMBERSHIP_UPDATE_REQUEST = """
            {
              "role": "OWNER"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizationMembershipUseCase organizationMembershipUseCaseMock;

    @MockitoBean
    private OrganizationMembershipWebMapper organizationMembershipWebMapperMock;

    @Test
    void testWhenOrganizationMembershipRequestValid_thenCreateMembership() throws Exception {
        var request = OrganizationMembershipTestDataFactory.createValidRequest();
        var input = OrganizationMembershipTestDataFactory.createValidInput();
        var output = OrganizationMembershipTestDataFactory.createOutput();
        var response = OrganizationMembershipTestDataFactory.createResponse();
        when(organizationMembershipWebMapperMock.toInput(request)).thenReturn(input);
        when(organizationMembershipUseCaseMock.createOrganizationMembership(TEST_ORGANIZATION_ID, input))
                .thenReturn(output);
        when(organizationMembershipWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(post(ORGANIZATION_MEMBERSHIPS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORGANIZATION_MEMBERSHIP_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(ORGANIZATION_ID_PATH).value(response.organizationId().toString()))
                .andExpect(jsonPath(USER_ID_PATH).value(response.userId().toString()))
                .andExpect(jsonPath(ROLE_PATH).value(response.role().name()));
    }

    @Test
    void testWhenOrganizationMembershipExists_thenReturnMembership() throws Exception {
        var output = OrganizationMembershipTestDataFactory.createOutput();
        var response = OrganizationMembershipTestDataFactory.createResponse();
        when(organizationMembershipUseCaseMock.getOrganizationMembership(TEST_MEMBERSHIP_ID)).thenReturn(output);
        when(organizationMembershipWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(get(ORGANIZATION_MEMBERSHIP_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()));
    }

    @Test
    void testWhenOrganizationMembershipsExist_thenReturnMemberships() throws Exception {
        var output = OrganizationMembershipTestDataFactory.createOutput();
        var response = OrganizationMembershipTestDataFactory.createResponse();
        when(organizationMembershipUseCaseMock.getOrganizationMemberships(TEST_ORGANIZATION_ID))
                .thenReturn(List.of(output));
        when(organizationMembershipWebMapperMock.toResponse(List.of(output))).thenReturn(List.of(response));

        mockMvc.perform(get(ORGANIZATION_MEMBERSHIPS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(FIRST_ID_PATH).value(response.id().toString()));
    }

    @Test
    void testWhenOrganizationMembershipUpdateValid_thenUpdateMembership() throws Exception {
        var request = OrganizationMembershipTestDataFactory.createValidUpdateRequest();
        var input = OrganizationMembershipTestDataFactory.createValidUpdateInput();
        var output = OrganizationMembershipTestDataFactory.createUpdatedOutput();
        var response = OrganizationMembershipTestDataFactory.createUpdatedResponse();
        when(organizationMembershipWebMapperMock.toInput(request)).thenReturn(input);
        when(organizationMembershipUseCaseMock.updateOrganizationMembership(TEST_MEMBERSHIP_ID, input))
                .thenReturn(output);
        when(organizationMembershipWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(put(ORGANIZATION_MEMBERSHIP_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORGANIZATION_MEMBERSHIP_UPDATE_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ROLE_PATH).value(response.role().name()));
    }

    @Test
    void testWhenOrganizationMembershipExists_thenDeleteMembership() throws Exception {
        mockMvc.perform(delete(ORGANIZATION_MEMBERSHIP_ENDPOINT))
                .andExpect(status().isNoContent());

        verify(organizationMembershipUseCaseMock).deleteOrganizationMembership(TEST_MEMBERSHIP_ID);
    }
}
