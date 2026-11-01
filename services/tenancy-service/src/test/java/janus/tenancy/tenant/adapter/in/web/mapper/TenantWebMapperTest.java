package janus.tenancy.tenant.adapter.in.web.mapper;

import janus.tenancy.tenant.testdata.TenantTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantWebMapperTest {

    private final TenantWebMapper mapper = Mappers.getMapper(TenantWebMapper.class);

    @Test
    void testWhenTenantRequestValid_thenReturnInput() {
        var request = TenantTestDataFactory.createValidRequest();
        var expected = TenantTestDataFactory.createValidInput();

        var result = mapper.toInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantUpdateRequestValid_thenReturnUpdateInput() {
        var request = TenantTestDataFactory.createValidUpdateRequest();
        var expected = TenantTestDataFactory.createValidUpdateInput();

        var result = mapper.toInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantOutputValid_thenReturnResponse() {
        var output = TenantTestDataFactory.createActiveOutput();
        var expected = TenantTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(output);

        assertEquals(expected, result);
    }

    @Test
    void testWhenTenantOutputsValid_thenReturnResponses() {
        var output = TenantTestDataFactory.createActiveOutput();
        var expected = TenantTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(List.of(output));

        assertEquals(List.of(expected), result);
    }
}
