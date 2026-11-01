package janus.tenancy.tenant.application.mapper;

import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory;
import janus.tenancy.tenant.testdata.TenantTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantMembershipMapperTest {

    private final TenantMembershipMapper mapper = Mappers.getMapper(TenantMembershipMapper.class);

    @Test
    void testWhenTenantMembershipInputValid_thenReturnActiveMembership() {
        var input = TenantMembershipTestDataFactory.createValidInput();
        var expected = TenantMembershipTestDataFactory.createNewActiveMembership();

        var result = mapper.toTenantMembership(TenantTestDataFactory.TEST_TENANT_ID, input);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantMembershipUpdateValid_thenReturnUpdatedMembership() {
        var membership = TenantMembershipTestDataFactory.createActiveMembership();
        var input = TenantMembershipTestDataFactory.createValidUpdateInput();
        var expected = TenantMembershipTestDataFactory.createUpdatedMembership();

        var result = mapper.toUpdatedTenantMembership(membership, input);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantMembershipStatusChanges_thenReturnMembershipWithUpdatedStatus() {
        var membership = TenantMembershipTestDataFactory.createActiveMembership();
        var expected = TenantMembershipTestDataFactory.createSuspendedMembership();

        var result = mapper.withUpdatedStatus(membership, TenantMembershipStatus.SUSPENDED);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantMembershipValid_thenReturnOutput() {
        var membership = TenantMembershipTestDataFactory.createActiveMembership();
        var expected = TenantMembershipTestDataFactory.createActiveOutput();

        var result = mapper.toOutput(membership);

        assertEquals(expected, result);
    }
}
