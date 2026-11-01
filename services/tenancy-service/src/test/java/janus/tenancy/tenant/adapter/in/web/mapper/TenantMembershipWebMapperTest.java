package janus.tenancy.tenant.adapter.in.web.mapper;

import janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantMembershipWebMapperTest {

    private final TenantMembershipWebMapper mapper = Mappers.getMapper(TenantMembershipWebMapper.class);

    @Test
    void testWhenTenantMembershipRequestValid_thenReturnInput() {
        var request = TenantMembershipTestDataFactory.createValidRequest();
        var expected = TenantMembershipTestDataFactory.createValidInput();

        var result = mapper.toInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantMembershipUpdateRequestValid_thenReturnUpdateInput() {
        var request = TenantMembershipTestDataFactory.createValidUpdateRequest();
        var expected = TenantMembershipTestDataFactory.createValidUpdateInput();

        var result = mapper.toInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantMembershipOutputValid_thenReturnResponse() {
        var output = TenantMembershipTestDataFactory.createActiveOutput();
        var expected = TenantMembershipTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(output);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantMembershipOutputsValid_thenReturnResponses() {
        var output = TenantMembershipTestDataFactory.createActiveOutput();
        var expected = TenantMembershipTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(List.of(output));

        assertEquals(List.of(expected), result);
    }
}
