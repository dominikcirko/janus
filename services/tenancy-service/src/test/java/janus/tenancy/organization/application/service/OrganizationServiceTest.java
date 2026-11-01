package janus.tenancy.organization.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.organization.application.mapper.OrganizationMapper;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import janus.tenancy.organization.application.port.usecase.model.OrganizationCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static janus.tenancy.organization.testdata.OrganizationTestDataFactory.TEST_ORGANIZATION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepositoryMock;
    @Mock
    private OrganizationMapper organizationMapperMock;
    @Mock
    private Organization organizationMock;
    @Mock
    private Organization mappedOrganizationMock;
    @Mock
    private Organization savedOrganizationMock;
    @Mock
    private OrganizationOutput outputMock;

    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        organizationService = new OrganizationService(organizationRepositoryMock, organizationMapperMock);
    }

    @Test
    void testWhenOrganizationInputValid_thenSaveOrganization() {
        var input = OrganizationTestDataFactory.createValidInput();
        when(organizationRepositoryMock.existsBySlug(input.slug())).thenReturn(false);
        when(organizationMapperMock.toOrganization(same(input))).thenReturn(mappedOrganizationMock);
        when(organizationRepositoryMock.save(mappedOrganizationMock)).thenReturn(savedOrganizationMock);
        when(organizationMapperMock.toOutput(savedOrganizationMock)).thenReturn(outputMock);

        var result = organizationService.createOrganization(input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenOrganizationInputNull_thenRejectCreation() {
        assertCreationRejected(null);
    }

    @Test
    void testWhenOrganizationNameBlank_thenRejectCreation() {
        var input = OrganizationTestDataFactory.createInputWithName(" ");

        assertCreationRejected(input);
    }

    @Test
    void testWhenOrganizationSlugBlank_thenRejectCreation() {
        var input = OrganizationTestDataFactory.createInputWithSlug(" ");

        assertCreationRejected(input);
    }

    @Test
    void testWhenOrganizationSlugAlreadyExists_thenRejectCreation() {
        var input = OrganizationTestDataFactory.createValidInput();
        when(organizationRepositoryMock.existsBySlug(input.slug())).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> organizationService.createOrganization(input)
        );

        verifyNoInteractions(organizationMapperMock);
    }

    @Test
    void testWhenOrganizationExists_thenReturnOrganization() {
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));
        when(organizationMapperMock.toOutput(organizationMock)).thenReturn(outputMock);

        var result = organizationService.getOrganization(TEST_ORGANIZATION_ID);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenOrganizationDoesNotExist_thenRejectRequest() {
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> organizationService.getOrganization(TEST_ORGANIZATION_ID)
        );

        verifyNoInteractions(organizationMapperMock);
    }

    @Test
    void testWhenOrganizationsExist_thenReturnOrganizations() {
        when(organizationRepositoryMock.findAll()).thenReturn(List.of(organizationMock));
        when(organizationMapperMock.toOutput(organizationMock)).thenReturn(outputMock);

        var result = organizationService.getOrganizations();

        assertEquals(List.of(outputMock), result);
    }

    @Test
    void testWhenOrganizationUpdateValid_thenSaveUpdatedOrganization() {
        var currentInput = OrganizationTestDataFactory.createValidInput();
        var updatedInput = OrganizationTestDataFactory.createUpdatedInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));
        when(organizationMock.getSlug()).thenReturn(currentInput.slug());
        when(organizationRepositoryMock.existsBySlug(updatedInput.slug())).thenReturn(false);
        when(organizationMapperMock.toUpdatedOrganization(organizationMock, updatedInput))
                .thenReturn(mappedOrganizationMock);
        when(organizationRepositoryMock.save(mappedOrganizationMock)).thenReturn(savedOrganizationMock);
        when(organizationMapperMock.toOutput(savedOrganizationMock)).thenReturn(outputMock);

        var result = organizationService.updateOrganization(TEST_ORGANIZATION_ID, updatedInput);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenUpdatedOrganizationSlugAlreadyExists_thenRejectUpdate() {
        var currentInput = OrganizationTestDataFactory.createValidInput();
        var updatedInput = OrganizationTestDataFactory.createUpdatedInput();
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));
        when(organizationMock.getSlug()).thenReturn(currentInput.slug());
        when(organizationRepositoryMock.existsBySlug(updatedInput.slug())).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> organizationService.updateOrganization(TEST_ORGANIZATION_ID, updatedInput)
        );

        verifyNoInteractions(organizationMapperMock);
    }

    @Test
    void testWhenOrganizationStatusValid_thenSaveChangedStatus() {
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));
        when(organizationMapperMock.withUpdatedStatus(organizationMock, OrganizationStatus.SUSPENDED))
                .thenReturn(mappedOrganizationMock);
        when(organizationRepositoryMock.save(mappedOrganizationMock)).thenReturn(savedOrganizationMock);
        when(organizationMapperMock.toOutput(savedOrganizationMock)).thenReturn(outputMock);

        var result = organizationService.changeOrganizationStatus(TEST_ORGANIZATION_ID, OrganizationStatus.SUSPENDED);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenOrganizationStatusNull_thenRejectStatusChange() {
        assertThrows(
                InvalidRequestException.class,
                () -> organizationService.changeOrganizationStatus(TEST_ORGANIZATION_ID, null)
        );

        verifyNoInteractions(organizationRepositoryMock, organizationMapperMock);
    }

    @Test
    void testWhenOrganizationExists_thenDeleteOrganization() {
        when(organizationRepositoryMock.findById(TEST_ORGANIZATION_ID))
                .thenReturn(Optional.of(organizationMock));

        organizationService.deleteOrganization(TEST_ORGANIZATION_ID);

        verify(organizationRepositoryMock).deleteById(TEST_ORGANIZATION_ID);
    }

    private void assertCreationRejected(OrganizationCreateInput input) {
        assertThrows(InvalidRequestException.class, () -> organizationService.createOrganization(input));
        verifyNoInteractions(organizationRepositoryMock, organizationMapperMock);
    }
}
