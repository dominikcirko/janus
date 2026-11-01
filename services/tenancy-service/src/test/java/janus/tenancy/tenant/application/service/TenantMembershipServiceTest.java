package janus.tenancy.tenant.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.tenant.application.mapper.TenantMembershipMapper;
import janus.tenancy.tenant.application.port.out.TenantMembershipRepository;
import janus.tenancy.tenant.application.port.out.TenantRepository;
import janus.tenancy.tenant.application.port.usecase.model.Tenant;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembership;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipUpdateInput;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory;
import janus.tenancy.tenant.testdata.TenantTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory.TEST_MEMBERSHIP_ID;
import static janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory.TEST_USER_ID;
import static janus.tenancy.tenant.testdata.TenantTestDataFactory.TEST_TENANT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TenantMembershipServiceTest {

    @Mock
    private TenantMembershipRepository tenantMembershipRepositoryMock;
    @Mock
    private TenantRepository tenantRepositoryMock;
    @Mock
    private TenantMembershipMapper tenantMembershipMapperMock;
    @Mock
    private Tenant tenantMock;
    @Mock
    private TenantMembership tenantMembershipMock;
    @Mock
    private TenantMembership mappedTenantMembershipMock;
    @Mock
    private TenantMembership savedTenantMembershipMock;
    @Mock
    private TenantMembershipOutput outputMock;

    private TenantMembershipService tenantMembershipService;

    @BeforeEach
    void setUp() {
        tenantMembershipService = new TenantMembershipService(
                tenantMembershipRepositoryMock,
                tenantRepositoryMock,
                tenantMembershipMapperMock
        );
    }

    @Test
    void testWhenTenantMembershipInputValid_thenSaveMembership() {
        var input = TenantMembershipTestDataFactory.createValidInput();
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.of(tenantMock));
        when(tenantMembershipMapperMock.toTenantMembership(TEST_TENANT_ID, input))
                .thenReturn(mappedTenantMembershipMock);
        when(tenantMembershipRepositoryMock.save(mappedTenantMembershipMock))
                .thenReturn(savedTenantMembershipMock);
        when(tenantMembershipMapperMock.toOutput(savedTenantMembershipMock)).thenReturn(outputMock);

        var result = tenantMembershipService.createTenantMembership(TEST_TENANT_ID, input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenTenantIdNull_thenRejectCreation() {
        assertCreationRejected(null, TenantMembershipTestDataFactory.createValidInput());
    }

    @Test
    void testWhenTenantMembershipInputNull_thenRejectCreation() {
        assertCreationRejected(TEST_TENANT_ID, null);
    }

    @Test
    void testWhenTenantMembershipUserIdNull_thenRejectCreation() {
        var input = TenantMembershipTestDataFactory.createInputWithUserId(null);

        assertCreationRejected(TEST_TENANT_ID, input);
    }

    @Test
    void testWhenTenantMembershipRoleNull_thenRejectCreation() {
        var input = TenantMembershipTestDataFactory.createInputWithRole(null);

        assertCreationRejected(TEST_TENANT_ID, input);
    }

    @Test
    void testWhenTenantMembershipMetadataNull_thenRejectCreation() {
        var input = TenantMembershipTestDataFactory.createInputWithMetadata(null);

        assertCreationRejected(TEST_TENANT_ID, input);
    }

    @Test
    void testWhenTenantDoesNotExist_thenRejectCreation() {
        var input = TenantMembershipTestDataFactory.createValidInput();
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantMembershipService.createTenantMembership(TEST_TENANT_ID, input)
        );

        verifyNoInteractions(tenantMembershipRepositoryMock, tenantMembershipMapperMock);
    }

    @Test
    void testWhenUserAlreadyBelongsToTenant_thenRejectCreation() {
        var input = TenantMembershipTestDataFactory.createValidInput();
        when(tenantRepositoryMock.findById(TEST_TENANT_ID)).thenReturn(Optional.of(tenantMock));
        when(tenantMembershipRepositoryMock.existsByTenantIdAndUserId(TEST_TENANT_ID, TEST_USER_ID))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> tenantMembershipService.createTenantMembership(TEST_TENANT_ID, input)
        );

        verifyNoInteractions(tenantMembershipMapperMock);
    }

    @Test
    void testWhenTenantMembershipExists_thenReturnMembership() {
        when(tenantMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID))
                .thenReturn(Optional.of(tenantMembershipMock));
        when(tenantMembershipMapperMock.toOutput(tenantMembershipMock)).thenReturn(outputMock);

        var result = tenantMembershipService.getTenantMembership(TEST_MEMBERSHIP_ID);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenTenantMembershipDoesNotExist_thenRejectRequest() {
        when(tenantMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tenantMembershipService.getTenantMembership(TEST_MEMBERSHIP_ID)
        );

        verifyNoInteractions(tenantMembershipMapperMock);
    }

    @Test
    void testWhenTenantMembershipsExist_thenReturnMemberships() {
        when(tenantMembershipRepositoryMock.findAllByTenantId(TEST_TENANT_ID))
                .thenReturn(List.of(tenantMembershipMock));
        when(tenantMembershipMapperMock.toOutput(tenantMembershipMock)).thenReturn(outputMock);

        var result = tenantMembershipService.getTenantMemberships(TEST_TENANT_ID);

        assertEquals(List.of(outputMock), result);
    }

    @Test
    void testWhenTenantIdNull_thenRejectMembershipListRequest() {
        assertThrows(InvalidRequestException.class, () -> tenantMembershipService.getTenantMemberships(null));

        verifyNoInteractions(tenantMembershipRepositoryMock, tenantRepositoryMock, tenantMembershipMapperMock);
    }

    @Test
    void testWhenTenantMembershipUpdateValid_thenSaveUpdatedMembership() {
        var input = TenantMembershipTestDataFactory.createValidUpdateInput();
        when(tenantMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID))
                .thenReturn(Optional.of(tenantMembershipMock));
        when(tenantMembershipMapperMock.toUpdatedTenantMembership(tenantMembershipMock, input))
                .thenReturn(mappedTenantMembershipMock);
        when(tenantMembershipRepositoryMock.save(mappedTenantMembershipMock))
                .thenReturn(savedTenantMembershipMock);
        when(tenantMembershipMapperMock.toOutput(savedTenantMembershipMock)).thenReturn(outputMock);

        var result = tenantMembershipService.updateTenantMembership(TEST_MEMBERSHIP_ID, input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenTenantMembershipUpdateInputNull_thenRejectUpdate() {
        assertUpdateRejected(null);
    }

    @Test
    void testWhenUpdatedTenantMembershipRoleNull_thenRejectUpdate() {
        var input = TenantMembershipTestDataFactory.createUpdateInputWithRole(null);

        assertUpdateRejected(input);
    }

    @Test
    void testWhenUpdatedTenantMembershipMetadataNull_thenRejectUpdate() {
        var input = TenantMembershipTestDataFactory.createUpdateInputWithMetadata(null);

        assertUpdateRejected(input);
    }

    @Test
    void testWhenTenantMembershipStatusValid_thenSaveChangedStatus() {
        when(tenantMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID))
                .thenReturn(Optional.of(tenantMembershipMock));
        when(tenantMembershipMapperMock.withUpdatedStatus(tenantMembershipMock, TenantMembershipStatus.SUSPENDED))
                .thenReturn(mappedTenantMembershipMock);
        when(tenantMembershipRepositoryMock.save(mappedTenantMembershipMock))
                .thenReturn(savedTenantMembershipMock);
        when(tenantMembershipMapperMock.toOutput(savedTenantMembershipMock)).thenReturn(outputMock);

        var result = tenantMembershipService.changeTenantMembershipStatus(
                TEST_MEMBERSHIP_ID,
                TenantMembershipStatus.SUSPENDED
        );

        assertSame(outputMock, result);
    }

    @Test
    void testWhenTenantMembershipStatusNull_thenRejectStatusChange() {
        assertThrows(
                InvalidRequestException.class,
                () -> tenantMembershipService.changeTenantMembershipStatus(TEST_MEMBERSHIP_ID, null)
        );

        verifyNoInteractions(tenantMembershipRepositoryMock, tenantRepositoryMock, tenantMembershipMapperMock);
    }

    @Test
    void testWhenTenantMembershipExists_thenDeleteMembership() {
        when(tenantMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID))
                .thenReturn(Optional.of(tenantMembershipMock));

        tenantMembershipService.deleteTenantMembership(TEST_MEMBERSHIP_ID);

        verify(tenantMembershipRepositoryMock).deleteById(TEST_MEMBERSHIP_ID);
    }

    private void assertCreationRejected(UUID tenantId, TenantMembershipCreateInput input) {
        assertThrows(
                InvalidRequestException.class,
                () -> tenantMembershipService.createTenantMembership(tenantId, input)
        );
        verifyNoInteractions(tenantMembershipRepositoryMock, tenantRepositoryMock, tenantMembershipMapperMock);
    }

    private void assertUpdateRejected(TenantMembershipUpdateInput input) {
        assertThrows(
                InvalidRequestException.class,
                () -> tenantMembershipService.updateTenantMembership(TEST_MEMBERSHIP_ID, input)
        );
        verifyNoInteractions(tenantMembershipRepositoryMock, tenantRepositoryMock, tenantMembershipMapperMock);
    }
}
