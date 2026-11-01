package janus.tenancy.organization.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.organization.application.mapper.OrganizationMembershipMapper;
import janus.tenancy.organization.application.port.out.OrganizationMembershipRepository;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembership;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipUpdateInput;
import janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory.TEST_MEMBERSHIP_ID;
import static janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory.TEST_USER_ID;
import static janus.tenancy.organization.testdata.OrganizationTestDataFactory.TEST_ORGANIZATION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizationMembershipServiceTest {

    @Mock
    private OrganizationMembershipRepository organizationMembershipRepositoryMock;
    @Mock
    private OrganizationRepository organizationRepositoryMock;
    @Mock
    private OrganizationMembershipMapper organizationMembershipMapperMock;
    @Mock
    private Organization organizationMock;
    @Mock
    private OrganizationMembership organizationMembershipMock;
    @Mock
    private OrganizationMembership mappedOrganizationMembershipMock;
    @Mock
    private OrganizationMembership savedOrganizationMembershipMock;
    @Mock
    private OrganizationMembershipOutput outputMock;

    private OrganizationMembershipService organizationMembershipService;

    @BeforeEach
    void setUp() {
        organizationMembershipService = new OrganizationMembershipService(
                organizationMembershipRepositoryMock,
                organizationRepositoryMock,
                organizationMembershipMapperMock
        );
    }

    @Test
    void testWhenOrganizationMembershipInputValid_thenSaveMembership() {
        var input = OrganizationMembershipTestDataFactory.createValidInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID)).thenReturn(Optional.of(organizationMock));
        when(organizationMembershipMapperMock.toOrganizationMembership(TEST_ORGANIZATION_ID, input))
                .thenReturn(mappedOrganizationMembershipMock);
        when(organizationMembershipRepositoryMock.save(mappedOrganizationMembershipMock))
                .thenReturn(savedOrganizationMembershipMock);
        when(organizationMembershipMapperMock.toOutput(savedOrganizationMembershipMock)).thenReturn(outputMock);

        var result = organizationMembershipService.createOrganizationMembership(TEST_ORGANIZATION_ID, input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenOrganizationIdNull_thenRejectCreation() {
        assertCreationRejected(null, OrganizationMembershipTestDataFactory.createValidInput());
    }

    @Test
    void testWhenOrganizationMembershipInputNull_thenRejectCreation() {
        assertCreationRejected(TEST_ORGANIZATION_ID, null);
    }

    @Test
    void testWhenOrganizationMembershipUserIdNull_thenRejectCreation() {
        var input = OrganizationMembershipTestDataFactory.createInputWithUserId(null);

        assertCreationRejected(TEST_ORGANIZATION_ID, input);
    }

    @Test
    void testWhenOrganizationMembershipRoleNull_thenRejectCreation() {
        var input = OrganizationMembershipTestDataFactory.createInputWithRole(null);

        assertCreationRejected(TEST_ORGANIZATION_ID, input);
    }

    @Test
    void testWhenOrganizationDoesNotExist_thenRejectCreation() {
        var input = OrganizationMembershipTestDataFactory.createValidInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> organizationMembershipService.createOrganizationMembership(TEST_ORGANIZATION_ID, input)
        );

        verifyNoInteractions(organizationMembershipRepositoryMock, organizationMembershipMapperMock);
    }

    @Test
    void testWhenUserAlreadyBelongsToOrganization_thenRejectCreation() {
        var input = OrganizationMembershipTestDataFactory.createValidInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID)).thenReturn(Optional.of(organizationMock));
        when(organizationMembershipRepositoryMock.existsByOrganizationIdAndUserId(
                TEST_ORGANIZATION_ID,
                TEST_USER_ID
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> organizationMembershipService.createOrganizationMembership(TEST_ORGANIZATION_ID, input)
        );

        verifyNoInteractions(organizationMembershipMapperMock);
    }

    @Test
    void testWhenOrganizationMembershipExists_thenReturnMembership() {
        when(organizationMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID))
                .thenReturn(Optional.of(organizationMembershipMock));
        when(organizationMembershipMapperMock.toOutput(organizationMembershipMock)).thenReturn(outputMock);

        var result = organizationMembershipService.getOrganizationMembership(TEST_MEMBERSHIP_ID);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenOrganizationMembershipDoesNotExist_thenRejectRequest() {
        when(organizationMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> organizationMembershipService.getOrganizationMembership(TEST_MEMBERSHIP_ID)
        );

        verifyNoInteractions(organizationMembershipMapperMock);
    }

    @Test
    void testWhenOrganizationMembershipsExist_thenReturnMemberships() {
        when(organizationMembershipRepositoryMock.findAllByOrganizationId(TEST_ORGANIZATION_ID))
                .thenReturn(List.of(organizationMembershipMock));
        when(organizationMembershipMapperMock.toOutput(organizationMembershipMock)).thenReturn(outputMock);

        var result = organizationMembershipService.getOrganizationMemberships(TEST_ORGANIZATION_ID);

        assertEquals(List.of(outputMock), result);
    }

    @Test
    void testWhenOrganizationIdNull_thenRejectMembershipListRequest() {
        assertThrows(
                InvalidRequestException.class,
                () -> organizationMembershipService.getOrganizationMemberships(null)
        );

        verifyNoInteractions(
                organizationMembershipRepositoryMock,
                organizationRepositoryMock,
                organizationMembershipMapperMock
        );
    }

    @Test
    void testWhenOrganizationMembershipUpdateValid_thenSaveUpdatedMembership() {
        var input = OrganizationMembershipTestDataFactory.createValidUpdateInput();
        when(organizationMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID))
                .thenReturn(Optional.of(organizationMembershipMock));
        when(organizationMembershipMapperMock.toUpdatedOrganizationMembership(organizationMembershipMock, input))
                .thenReturn(mappedOrganizationMembershipMock);
        when(organizationMembershipRepositoryMock.save(mappedOrganizationMembershipMock))
                .thenReturn(savedOrganizationMembershipMock);
        when(organizationMembershipMapperMock.toOutput(savedOrganizationMembershipMock)).thenReturn(outputMock);

        var result = organizationMembershipService.updateOrganizationMembership(TEST_MEMBERSHIP_ID, input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenOrganizationMembershipUpdateInputNull_thenRejectUpdate() {
        assertUpdateRejected(null);
    }

    @Test
    void testWhenUpdatedOrganizationMembershipRoleNull_thenRejectUpdate() {
        var input = OrganizationMembershipTestDataFactory.createUpdateInputWithRole(null);

        assertUpdateRejected(input);
    }

    @Test
    void testWhenOrganizationMembershipExists_thenDeleteMembership() {
        when(organizationMembershipRepositoryMock.findById(TEST_MEMBERSHIP_ID))
                .thenReturn(Optional.of(organizationMembershipMock));

        organizationMembershipService.deleteOrganizationMembership(TEST_MEMBERSHIP_ID);

        verify(organizationMembershipRepositoryMock).deleteById(TEST_MEMBERSHIP_ID);
    }

    private void assertCreationRejected(UUID organizationId, OrganizationMembershipCreateInput input) {
        assertThrows(
                InvalidRequestException.class,
                () -> organizationMembershipService.createOrganizationMembership(organizationId, input)
        );
        verifyNoInteractions(
                organizationMembershipRepositoryMock,
                organizationRepositoryMock,
                organizationMembershipMapperMock
        );
    }

    private void assertUpdateRejected(OrganizationMembershipUpdateInput input) {
        assertThrows(
                InvalidRequestException.class,
                () -> organizationMembershipService.updateOrganizationMembership(TEST_MEMBERSHIP_ID, input)
        );
        verifyNoInteractions(
                organizationMembershipRepositoryMock,
                organizationRepositoryMock,
                organizationMembershipMapperMock
        );
    }
}
