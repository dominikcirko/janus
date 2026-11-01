package janus.tenancy.organization.application.mapper;

import janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory;
import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrganizationMembershipMapperTest {

    private final OrganizationMembershipMapper mapper = Mappers.getMapper(OrganizationMembershipMapper.class);

    @Test
    void testWhenOrganizationMembershipInputValid_thenReturnMembership() {
        var input = OrganizationMembershipTestDataFactory.createValidInput();
        var expected = OrganizationMembershipTestDataFactory.createNewMembership();

        var result = mapper.toOrganizationMembership(OrganizationTestDataFactory.TEST_ORGANIZATION_ID, input);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOrganizationMembershipUpdateValid_thenReturnUpdatedMembership() {
        var membership = OrganizationMembershipTestDataFactory.createMembership();
        var input = OrganizationMembershipTestDataFactory.createValidUpdateInput();
        var expected = OrganizationMembershipTestDataFactory.createUpdatedMembership();

        var result = mapper.toUpdatedOrganizationMembership(membership, input);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOrganizationMembershipValid_thenReturnOutput() {
        var membership = OrganizationMembershipTestDataFactory.createMembership();
        var expected = OrganizationMembershipTestDataFactory.createOutput();

        var result = mapper.toOutput(membership);

        assertEquals(expected, result);
    }
}
