package janus.tenancy.organization.adapter.in.web;

import janus.common.config.ApiVersioningConfig;
import janus.tenancy.organization.adapter.in.web.mapper.OrganizationWebMapper;
import janus.tenancy.organization.application.port.usecase.OrganizationUseCase;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static janus.tenancy.organization.testdata.OrganizationTestDataFactory.TEST_ORGANIZATION_ID;
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

@WebMvcTest(OrganizationController.class)
@Import(ApiVersioningConfig.class)
public class OrganizationControllerTest {

    private static final String ORGANIZATIONS_ENDPOINT = "/api/v1/organizations";
    private static final String ORGANIZATION_ENDPOINT = ORGANIZATIONS_ENDPOINT + "/" + TEST_ORGANIZATION_ID;
    private static final String ORGANIZATION_STATUS_ENDPOINT = ORGANIZATION_ENDPOINT + "/status";

    private static final String ID_PATH = "$.id";
    private static final String NAME_PATH = "$.name";
    private static final String SLUG_PATH = "$.slug";
    private static final String STATUS_PATH = "$.status";
    private static final String FIRST_ID_PATH = "$[0].id";
    private static final String FIRST_NAME_PATH = "$[0].name";
    private static final String FIRST_SLUG_PATH = "$[0].slug";
    private static final String FIRST_STATUS_PATH = "$[0].status";

    private static final String VALID_ORGANIZATION_REQUEST = """
            {
              "name": "Test Organization",
              "slug": "test-organization"
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
    private OrganizationUseCase organizationUseCaseMock;

    @MockitoBean
    private OrganizationWebMapper organizationWebMapperMock;

    @Test
    void testWhenOrganizationRequestValid_thenCreateOrganization() throws Exception {
        var request = OrganizationTestDataFactory.createValidRequest();
        var input = OrganizationTestDataFactory.createValidInput();
        var output = OrganizationTestDataFactory.createActiveOutput();
        var response = OrganizationTestDataFactory.createActiveResponse();
        when(organizationWebMapperMock.toCreateInput(request)).thenReturn(input);
        when(organizationUseCaseMock.createOrganization(input)).thenReturn(output);
        when(organizationWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(post(ORGANIZATIONS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORGANIZATION_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(NAME_PATH).value(response.name()))
                .andExpect(jsonPath(SLUG_PATH).value(response.slug()))
                .andExpect(jsonPath(STATUS_PATH).value(response.status().name()));
    }

    @Test
    void testWhenOrganizationExists_thenReturnOrganization() throws Exception {
        var output = OrganizationTestDataFactory.createActiveOutput();
        var response = OrganizationTestDataFactory.createActiveResponse();
        when(organizationUseCaseMock.getOrganization(TEST_ORGANIZATION_ID)).thenReturn(output);
        when(organizationWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(get(ORGANIZATION_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(NAME_PATH).value(response.name()))
                .andExpect(jsonPath(SLUG_PATH).value(response.slug()))
                .andExpect(jsonPath(STATUS_PATH).value(response.status().name()));
    }

    @Test
    void testWhenOrganizationsExist_thenReturnOrganizations() throws Exception {
        var output = OrganizationTestDataFactory.createActiveOutput();
        var response = OrganizationTestDataFactory.createActiveResponse();
        when(organizationUseCaseMock.getOrganizations()).thenReturn(List.of(output));
        when(organizationWebMapperMock.toResponse(List.of(output))).thenReturn(List.of(response));

        mockMvc.perform(get(ORGANIZATIONS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(FIRST_ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(FIRST_NAME_PATH).value(response.name()))
                .andExpect(jsonPath(FIRST_SLUG_PATH).value(response.slug()))
                .andExpect(jsonPath(FIRST_STATUS_PATH).value(response.status().name()));
    }

    @Test
    void testWhenOrganizationUpdateValid_thenUpdateOrganization() throws Exception {
        var request = OrganizationTestDataFactory.createValidRequest();
        var input = OrganizationTestDataFactory.createUpdatedInput();
        var output = OrganizationTestDataFactory.createActiveOutput();
        var response = OrganizationTestDataFactory.createActiveResponse();
        when(organizationWebMapperMock.toUpdateInput(request)).thenReturn(input);
        when(organizationUseCaseMock.updateOrganization(TEST_ORGANIZATION_ID, input)).thenReturn(output);
        when(organizationWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(put(ORGANIZATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORGANIZATION_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(NAME_PATH).value(response.name()))
                .andExpect(jsonPath(SLUG_PATH).value(response.slug()))
                .andExpect(jsonPath(STATUS_PATH).value(response.status().name()));
    }

    @Test
    void testWhenOrganizationStatusValid_thenChangeOrganizationStatus() throws Exception {
        var output = OrganizationTestDataFactory.createSuspendedOutput();
        var response = OrganizationTestDataFactory.createSuspendedResponse();
        when(organizationUseCaseMock.changeOrganizationStatus(TEST_ORGANIZATION_ID, OrganizationStatus.SUSPENDED))
                .thenReturn(output);
        when(organizationWebMapperMock.toResponse(output)).thenReturn(response);

        mockMvc.perform(patch(ORGANIZATION_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SUSPENDED_STATUS_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(ID_PATH).value(response.id().toString()))
                .andExpect(jsonPath(NAME_PATH).value(response.name()))
                .andExpect(jsonPath(SLUG_PATH).value(response.slug()))
                .andExpect(jsonPath(STATUS_PATH).value(response.status().name()));
    }

    @Test
    void testWhenOrganizationExists_thenDeleteOrganization() throws Exception {
        mockMvc.perform(delete(ORGANIZATION_ENDPOINT))
                .andExpect(status().isNoContent());

        verify(organizationUseCaseMock).deleteOrganization(TEST_ORGANIZATION_ID);
    }
}
