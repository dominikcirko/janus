package janus.tenancy.tenant.adapter.in.web;

import janus.common.config.ApiVersioningConfig;
import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import janus.tenancy.tenant.adapter.in.web.mapper.TenantWebMapper;
import janus.tenancy.tenant.application.port.usecase.TenantUseCase;
import janus.tenancy.tenant.domain.type.TenantStatus;
import janus.tenancy.tenant.testdata.TenantTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

@WebMvcTest(TenantController.class)
@Import(ApiVersioningConfig.class)
public class TenantControllerTest {

    private static final String ORGANIZATION_TENANTS_ENDPOINT =
            "/api/v1/organizations/" + OrganizationTestDataFactory.TEST_ORGANIZATION_ID + "/tenants";
    private static final String TENANT_ENDPOINT = "/api/v1/tenants/" + TEST_TENANT_ID;
    private static final String TENANT_STATUS_ENDPOINT = TENANT_ENDPOINT + "/status";

    private static final String ID_PATH = "$.id";
    private static final String ORGANIZATION_ID_PATH = "$.organizationId";
    private static final String NAME_PATH = "$.name";
    private static final String SLUG_PATH = "$.slug";
    private static final String ENVIRONMENT_PATH = "$.environment";
    private static final String STATUS_PATH = "$.status";
    private static final String ISSUER_PATH = "$.issuer";
    private static final String FIRST_ID_PATH = "$[0].id";

    private static final String VALID_TENANT_REQUEST = """
            {
              "name": "Test Tenant",
              "slug": "test-tenant",
              "environment": "DEVELOPMENT",
              "issuer": "https://janus.example.com/test-tenant"
            }
            """;

    private static final String VALID_TENANT_UPDATE_REQUEST = """
            {
              "name": "Updated Tenant",
              "slug": "updated-tenant",
              "environment": "STAGING"
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
    private TenantUseCase tenantUseCaseMock;

    @MockitoBean
    private TenantWebMapper tenantWebMapperMock;

    @Test
    void testWhenTenantRequestValid_thenCreateTenant() throws Exception {
        var request = TenantTestDataFactory.createValidRequest();
        var input = TenantTestDataFactory.createValidInput();
        var output = TenantTestDataFactory.createActiveOutput();
        var response = TenantTestDataFactory.createActiveResponse();
        when(tenantWebMapperMock.toInput(request)).thenReturn(input);
        when(tenantUseCaseMock.createTenant(OrganizationTestDataFactory.TEST_ORGANIZATION_ID, input))
                .thenReturn(output);
        when(tenantWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(post(ORGANIZATION_TENANTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_TENANT_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(ORGANIZATION_ID_PATH).value(response.organizationId().toString()))
                .andExpect(jsonPath(NAME_PATH).value(response.name()))
                .andExpect(jsonPath(SLUG_PATH).value(response.slug()))
                .andExpect(jsonPath(ENVIRONMENT_PATH).value(response.environment().name()))
                .andExpect(jsonPath(STATUS_PATH).value(response.status().name()))
                .andExpect(jsonPath(ISSUER_PATH).value(response.issuer()));
    }

    @Test
    void testWhenTenantExists_thenReturnTenant() throws Exception {
        var output = TenantTestDataFactory.createActiveOutput();
        var response = TenantTestDataFactory.createActiveResponse();
        when(tenantUseCaseMock.getTenant(TEST_TENANT_ID)).thenReturn(output);
        when(tenantWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(get(TENANT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()));
    }

    @Test
    void testWhenTenantsExist_thenReturnTenants() throws Exception {
        var output = TenantTestDataFactory.createActiveOutput();
        var response = TenantTestDataFactory.createActiveResponse();
        when(tenantUseCaseMock.getTenants(OrganizationTestDataFactory.TEST_ORGANIZATION_ID))
                .thenReturn(List.of(output));
        when(tenantWebMapperMock.toResponse(List.of(output))).thenReturn(List.of(response));

        mockMvc.perform(get(ORGANIZATION_TENANTS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(FIRST_ID_PATH).value(response.id().toString()));
    }

    @Test
    void testWhenTenantUpdateValid_thenUpdateTenant() throws Exception {
        var request = TenantTestDataFactory.createValidUpdateRequest();
        var input = TenantTestDataFactory.createValidUpdateInput();
        var output = TenantTestDataFactory.createUpdatedOutput();
        var response = TenantTestDataFactory.createUpdatedResponse();
        when(tenantWebMapperMock.toInput(request)).thenReturn(input);
        when(tenantUseCaseMock.updateTenant(TEST_TENANT_ID, input)).thenReturn(output);
        when(tenantWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(put(TENANT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_TENANT_UPDATE_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(NAME_PATH).value(response.name()))
                .andExpect(jsonPath(ENVIRONMENT_PATH).value(response.environment().name()));
    }

    @Test
    void testWhenTenantStatusValid_thenChangeTenantStatus() throws Exception {
        var output = TenantTestDataFactory.createSuspendedOutput();
        var response = TenantTestDataFactory.createSuspendedResponse();
        when(tenantUseCaseMock.changeTenantStatus(TEST_TENANT_ID, TenantStatus.SUSPENDED))
                .thenReturn(output);
        when(tenantWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(patch(TENANT_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SUSPENDED_STATUS_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(STATUS_PATH).value(TenantStatus.SUSPENDED.name()));
    }

    @Test
    void testWhenTenantExists_thenDeleteTenant() throws Exception {
        mockMvc.perform(delete(TENANT_ENDPOINT))
                .andExpect(status().isNoContent());

        verify(tenantUseCaseMock).deleteTenant(TEST_TENANT_ID);
    }
}
