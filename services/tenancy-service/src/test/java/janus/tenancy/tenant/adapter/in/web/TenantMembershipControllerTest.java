package janus.tenancy.tenant.adapter.in.web;

import janus.common.config.ApiVersioningConfig;
import janus.tenancy.tenant.adapter.in.web.mapper.TenantMembershipWebMapper;
import janus.tenancy.tenant.application.port.usecase.TenantMembershipUseCase;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory.TEST_MEMBERSHIP_ID;
import static janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory.TEST_USER_ID;
import static janus.tenancy.tenant.testdata.TenantTestDataFactory.TEST_TENANT_ID;
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

@WebMvcTest(TenantMembershipController.class)
@Import(ApiVersioningConfig.class)
public class TenantMembershipControllerTest {

    private static final String TENANT_MEMBERSHIPS_ENDPOINT =
            "/api/v1/tenants/" + TEST_TENANT_ID + "/memberships";
    private static final String TENANT_MEMBERSHIP_ENDPOINT =
            "/api/v1/tenant-memberships/" + TEST_MEMBERSHIP_ID;
    private static final String TENANT_MEMBERSHIP_STATUS_ENDPOINT = TENANT_MEMBERSHIP_ENDPOINT + "/status";

    private static final String ID_PATH = "$.id";
    private static final String TENANT_ID_PATH = "$.tenantId";
    private static final String USER_ID_PATH = "$.userId";
    private static final String ROLE_PATH = "$.role";
    private static final String STATUS_PATH = "$.status";
    private static final String METADATA_DEPARTMENT_PATH = "$.metadata.department";
    private static final String FIRST_ID_PATH = "$[0].id";

    private static final String VALID_TENANT_MEMBERSHIP_REQUEST = """
            {
              "userId": "%s",
              "role": "MEMBER",
              "metadata": {
                "department": "engineering"
              }
            }
            """.formatted(TEST_USER_ID);

    private static final String VALID_TENANT_MEMBERSHIP_UPDATE_REQUEST = """
            {
              "role": "ADMIN",
              "metadata": {
                "department": "platform"
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
    private TenantMembershipUseCase tenantMembershipUseCaseMock;

    @MockitoBean
    private TenantMembershipWebMapper tenantMembershipWebMapperMock;

    @Test
    void testWhenTenantMembershipRequestValid_thenCreateMembership() throws Exception {
        var request = TenantMembershipTestDataFactory.createValidRequest();
        var input = TenantMembershipTestDataFactory.createValidInput();
        var output = TenantMembershipTestDataFactory.createActiveOutput();
        var response = TenantMembershipTestDataFactory.createActiveResponse();
        when(tenantMembershipWebMapperMock.toInput(request)).thenReturn(input);
        when(tenantMembershipUseCaseMock.createTenantMembership(TEST_TENANT_ID, input)).thenReturn(output);
        when(tenantMembershipWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(post(TENANT_MEMBERSHIPS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_TENANT_MEMBERSHIP_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(TENANT_ID_PATH).value(response.tenantId().toString()))
                .andExpect(jsonPath(USER_ID_PATH).value(response.userId().toString()))
                .andExpect(jsonPath(ROLE_PATH).value(response.role().name()))
                .andExpect(jsonPath(STATUS_PATH).value(response.status().name()))
                .andExpect(jsonPath(METADATA_DEPARTMENT_PATH).value(response.metadata().get("department")));
    }

    @Test
    void testWhenTenantMembershipExists_thenReturnMembership() throws Exception {
        var output = TenantMembershipTestDataFactory.createActiveOutput();
        var response = TenantMembershipTestDataFactory.createActiveResponse();
        when(tenantMembershipUseCaseMock.getTenantMembership(TEST_MEMBERSHIP_ID)).thenReturn(output);
        when(tenantMembershipWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(get(TENANT_MEMBERSHIP_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()));
    }

    @Test
    void testWhenTenantMembershipsExist_thenReturnMemberships() throws Exception {
        var output = TenantMembershipTestDataFactory.createActiveOutput();
        var response = TenantMembershipTestDataFactory.createActiveResponse();
        when(tenantMembershipUseCaseMock.getTenantMemberships(TEST_TENANT_ID)).thenReturn(List.of(output));
        when(tenantMembershipWebMapperMock.toResponse(List.of(output))).thenReturn(List.of(response));

        mockMvc.perform(get(TENANT_MEMBERSHIPS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(FIRST_ID_PATH).value(response.id().toString()));
    }

    @Test
    void testWhenTenantMembershipUpdateValid_thenUpdateMembership() throws Exception {
        var request = TenantMembershipTestDataFactory.createValidUpdateRequest();
        var input = TenantMembershipTestDataFactory.createValidUpdateInput();
        var output = TenantMembershipTestDataFactory.createUpdatedOutput();
        var response = TenantMembershipTestDataFactory.createUpdatedResponse();
        when(tenantMembershipWebMapperMock.toInput(request)).thenReturn(input);
        when(tenantMembershipUseCaseMock.updateTenantMembership(TEST_MEMBERSHIP_ID, input)).thenReturn(output);
        when(tenantMembershipWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(put(TENANT_MEMBERSHIP_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_TENANT_MEMBERSHIP_UPDATE_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ROLE_PATH).value(response.role().name()))
                .andExpect(jsonPath(METADATA_DEPARTMENT_PATH).value(response.metadata().get("department")));
    }

    @Test
    void testWhenTenantMembershipStatusValid_thenChangeMembershipStatus() throws Exception {
        var output = TenantMembershipTestDataFactory.createSuspendedOutput();
        var response = TenantMembershipTestDataFactory.createSuspendedResponse();
        when(tenantMembershipUseCaseMock.changeTenantMembershipStatus(
                TEST_MEMBERSHIP_ID,
                TenantMembershipStatus.SUSPENDED
        )).thenReturn(output);
        when(tenantMembershipWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(patch(TENANT_MEMBERSHIP_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SUSPENDED_STATUS_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(STATUS_PATH).value(TenantMembershipStatus.SUSPENDED.name()));
    }

    @Test
    void testWhenTenantMembershipExists_thenDeleteMembership() throws Exception {
        mockMvc.perform(delete(TENANT_MEMBERSHIP_ENDPOINT))
                .andExpect(status().isNoContent());

        verify(tenantMembershipUseCaseMock).deleteTenantMembership(TEST_MEMBERSHIP_ID);
    }
}
