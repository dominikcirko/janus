package janus.tenancy.tenant.application.mapper;

import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import janus.tenancy.tenant.domain.type.TenantStatus;
import janus.tenancy.tenant.testdata.TenantTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantMapperTest {

    private final TenantMapper mapper = Mappers.getMapper(TenantMapper.class);

    @Test
    void testWhenTenantInputValid_thenReturnActiveTenant() {
        var input = TenantTestDataFactory.createValidInput();
        var expected = TenantTestDataFactory.createNewActiveTenant();

        var result = mapper.toTenant(OrganizationTestDataFactory.TEST_ORGANIZATION_ID, input);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantUpdateValid_thenReturnUpdatedTenant() {
        var tenant = TenantTestDataFactory.createActiveTenant();
        var input = TenantTestDataFactory.createValidUpdateInput();
        var expected = TenantTestDataFactory.createUpdatedTenant();

        var result = mapper.toUpdatedTenant(tenant, input);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantStatusChanges_thenReturnTenantWithUpdatedStatus() {
        var tenant = TenantTestDataFactory.createActiveTenant();
        var expected = TenantTestDataFactory.createSuspendedTenant();

        var result = mapper.withUpdatedStatus(tenant, TenantStatus.SUSPENDED);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantValid_thenReturnOutput() {
        var tenant = TenantTestDataFactory.createActiveTenant();
        var expected = TenantTestDataFactory.createActiveOutput();

        var result = mapper.toOutput(tenant);

        assertEquals(expected, result);
    }
}
