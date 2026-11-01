package janus.tenancy.tenant.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import janus.tenancy.tenant.application.mapper.TenantMapper;
import janus.tenancy.tenant.application.port.out.TenantRepository;
import janus.tenancy.tenant.application.port.usecase.model.Tenant;
import janus.tenancy.tenant.application.port.usecase.model.TenantCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantOutput;
import janus.tenancy.tenant.domain.type.TenantStatus;
import janus.tenancy.tenant.testdata.TenantTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static janus.tenancy.tenant.testdata.TenantTestDataFactory.TEST_TENANT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {

    private static final UUID TEST_ORGANIZATION_ID = OrganizationTestDataFactory.TEST_ORGANIZATION_ID;

    @Mock
    private TenantRepository tenantRepositoryMock;
    @Mock
    private OrganizationRepository organizationRepositoryMock;
    @Mock
    private TenantMapper tenantMapperMock;
    @Mock
    private Organization organizationMock;
    @Mock
    private Tenant tenantMock;
    @Mock
    private Tenant mappedTenantMock;
    @Mock
    private Tenant savedTenantMock;
    @Mock
    private TenantOutput outputMock;

    private TenantService tenantService;

    @BeforeEach
    void setUp() {
        tenantService = new TenantService(tenantRepositoryMock, organizationRepositoryMock, tenantMapperMock);
    }

    @Test
    void testWhenTenantInputValid_thenSaveTenant() {
        var input = TenantTestDataFactory.createValidInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));
        when(tenantMapperMock.toTenant(TEST_ORGANIZATION_ID, input)).thenReturn(mappedTenantMock);
        when(tenantRepositoryMock.save(mappedTenantMock)).thenReturn(savedTenantMock);
        when(tenantMapperMock.toOutput(savedTenantMock)).thenReturn(outputMock);

        var result = tenantService.createTenant(TEST_ORGANIZATION_ID, input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenOrganizationIdNull_thenRejectCreation() {
        assertCreationRejected(null, TenantTestDataFactory.createValidInput());
    }

    @Test
    void testWhenTenantInputNull_thenRejectCreation() {
        assertCreationRejected(TEST_ORGANIZATION_ID, null);
    }

    @Test
    void testWhenTenantNameBlank_thenRejectCreation() {
        var input = TenantTestDataFactory.createInputWithName(" ");

        assertCreationRejected(TEST_ORGANIZATION_ID, input);
    }

    @Test
    void testWhenTenantSlugBlank_thenRejectCreation() {
        var input = TenantTestDataFactory.createInputWithSlug(" ");

        assertCreationRejected(TEST_ORGANIZATION_ID, input);
    }

    @Test
    void testWhenTenantEnvironmentNull_thenRejectCreation() {
        var input = TenantTestDataFactory.createInputWithEnvironment(null);

        assertCreationRejected(TEST_ORGANIZATION_ID, input);
    }

    @Test
    void testWhenTenantIssuerBlank_thenRejectCreation() {
        var input = TenantTestDataFactory.createInputWithIssuer(" ");

        assertCreationRejected(TEST_ORGANIZATION_ID, input);
    }

    @Test
    void testWhenOrganizationDoesNotExist_thenRejectCreation() {
        var input = TenantTestDataFactory.createValidInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantService.createTenant(TEST_ORGANIZATION_ID, input)
        );

        verifyNoInteractions(tenantRepositoryMock, tenantMapperMock);
    }

    @Test
    void testWhenTenantSlugAlreadyExists_thenRejectCreation() {
        var input = TenantTestDataFactory.createValidInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));
        when(tenantRepositoryMock.existsByOrganizationIdAndSlug(TEST_ORGANIZATION_ID, input.slug()))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> tenantService.createTenant(TEST_ORGANIZATION_ID, input)
        );

        verifyNoInteractions(tenantMapperMock);
    }

    @Test
    void testWhenTenantIssuerAlreadyExists_thenRejectCreation() {
        var input = TenantTestDataFactory.createValidInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));
        when(tenantRepositoryMock.existsByIssuer(input.issuer())).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> tenantService.createTenant(TEST_ORGANIZATION_ID, input)
        );

        verifyNoInteractions(tenantMapperMock);
    }

    @Test
    void testWhenTenantExists_thenReturnTenant() {
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.of(tenantMock));
        when(tenantMapperMock.toOutput(tenantMock)).thenReturn(outputMock);

        var result = tenantService.getTenant(TEST_TENANT_ID);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenTenantDoesNotExist_thenRejectRequest() {
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tenantService.getTenant(TEST_TENANT_ID));

        verifyNoInteractions(tenantMapperMock);
    }

    @Test
    void testWhenTenantsExist_thenReturnTenants() {
        when(tenantRepositoryMock.findAllByOrganizationId(TEST_ORGANIZATION_ID))
                .thenReturn(List.of(tenantMock));
        when(tenantMapperMock.toOutput(tenantMock)).thenReturn(outputMock);

        var result = tenantService.getTenants(TEST_ORGANIZATION_ID);

        assertEquals(List.of(outputMock), result);
    }

    @Test
    void testWhenTenantUpdateValid_thenSaveUpdatedTenant() {
        var input = TenantTestDataFactory.createValidUpdateInput();
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.of(tenantMock));
        when(tenantMock.getSlug()).thenReturn(TenantTestDataFactory.createValidInput().slug());
        when(tenantMock.getOrganizationId()).thenReturn(TEST_ORGANIZATION_ID);
        when(tenantMapperMock.toUpdatedTenant(tenantMock, input)).thenReturn(mappedTenantMock);
        when(tenantRepositoryMock.save(mappedTenantMock)).thenReturn(savedTenantMock);
        when(tenantMapperMock.toOutput(savedTenantMock)).thenReturn(outputMock);

        var result = tenantService.updateTenant(TEST_TENANT_ID, input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenTenantUpdateInputNull_thenRejectUpdate() {
        assertThrows(InvalidRequestException.class, () -> tenantService.updateTenant(TEST_TENANT_ID, null));

        verifyNoInteractions(tenantRepositoryMock, tenantMapperMock);
    }

    @Test
    void testWhenUpdatedTenantSlugAlreadyExists_thenRejectUpdate() {
        var input = TenantTestDataFactory.createValidUpdateInput();
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.of(tenantMock));
        when(tenantMock.getSlug()).thenReturn(TenantTestDataFactory.createValidInput().slug());
        when(tenantMock.getOrganizationId()).thenReturn(TEST_ORGANIZATION_ID);
        when(tenantRepositoryMock.existsByOrganizationIdAndSlug(TEST_ORGANIZATION_ID, input.slug()))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> tenantService.updateTenant(TEST_TENANT_ID, input)
        );

        verifyNoInteractions(tenantMapperMock);
    }

    @Test
    void testWhenTenantStatusValid_thenSaveChangedStatus() {
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.of(tenantMock));
        when(tenantMapperMock.withUpdatedStatus(tenantMock, TenantStatus.SUSPENDED))
                .thenReturn(mappedTenantMock);
        when(tenantRepositoryMock.save(mappedTenantMock)).thenReturn(savedTenantMock);
        when(tenantMapperMock.toOutput(savedTenantMock)).thenReturn(outputMock);

        var result = tenantService.changeTenantStatus(TEST_TENANT_ID, TenantStatus.SUSPENDED);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenTenantStatusNull_thenRejectStatusChange() {
        assertThrows(
                InvalidRequestException.class,
                () -> tenantService.changeTenantStatus(TEST_TENANT_ID, null)
        );

        verifyNoInteractions(tenantRepositoryMock, tenantMapperMock);
    }

    @Test
    void testWhenTenantExists_thenDeleteTenant() {
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.of(tenantMock));

        tenantService.deleteTenant(TEST_TENANT_ID);

        verify(tenantRepositoryMock).deleteById(TEST_TENANT_ID);
    }

    private void assertCreationRejected(UUID organizationId, TenantCreateInput input) {
        assertThrows(InvalidRequestException.class, () -> tenantService.createTenant(organizationId, input));
        verifyNoInteractions(tenantRepositoryMock, organizationRepositoryMock, tenantMapperMock);
    }
}
