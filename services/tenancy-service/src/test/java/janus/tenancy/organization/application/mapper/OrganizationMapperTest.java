package janus.tenancy.organization.application.mapper;

import janus.tenancy.organization.domain.type.OrganizationStatus;
import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static janus.tenancy.organization.testdata.OrganizationTestDataFactory.TEST_ORGANIZATION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrganizationMapperTest {

    private final OrganizationMapper mapper = Mappers.getMapper(OrganizationMapper.class);

    @Test
    void testWhenOrganizationInputValid_thenReturnActiveOrganization() {
        var input = OrganizationTestDataFactory.createValidInput();

        var result = mapper.toOrganization(input);

        assertNull(result.getId());
        assertEquals(input.name(), result.getName());
        assertEquals(input.slug(), result.getSlug());
        assertEquals(OrganizationStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testWhenOrganizationUpdateValid_thenReturnUpdatedOrganization() {
        var organization = OrganizationTestDataFactory.createActiveOrganization();
        var input = OrganizationTestDataFactory.createUpdatedInput();

        var result = mapper.toUpdatedOrganization(organization, input);

        assertEquals(TEST_ORGANIZATION_ID, result.getId());
        assertEquals(input.name(), result.getName());
        assertEquals(input.slug(), result.getSlug());
        assertEquals(OrganizationStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testWhenOrganizationStatusChanges_thenReturnOrganizationWithNewStatus() {
        var organization = OrganizationTestDataFactory.createActiveOrganization();

        var result = mapper.withUpdatedStatus(organization, OrganizationStatus.SUSPENDED);

        assertEquals(organization.getId(), result.getId());
        assertEquals(organization.getName(), result.getName());
        assertEquals(organization.getSlug(), result.getSlug());
        assertEquals(OrganizationStatus.SUSPENDED, result.getStatus());
    }

    @Test
    void testWhenOrganizationValid_thenReturnOutput() {
        var organization = OrganizationTestDataFactory.createActiveOrganization();
        var expected = OrganizationTestDataFactory.createActiveOutput();

        var result = mapper.toOutput(organization);

        assertEquals(expected, result);
    }
}
