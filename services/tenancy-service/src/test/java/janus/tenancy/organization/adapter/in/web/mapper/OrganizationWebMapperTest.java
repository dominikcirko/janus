package janus.tenancy.organization.adapter.in.web.mapper;

import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrganizationWebMapperTest {

    private final OrganizationWebMapper mapper = Mappers.getMapper(OrganizationWebMapper.class);

    @Test
    void testWhenRequestValid_thenReturnInput() {
        var request = OrganizationTestDataFactory.createValidRequest();
        var expected = OrganizationTestDataFactory.createValidInput();

        var result = mapper.toCreateInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOutputValid_thenReturnResponse() {
        var output = OrganizationTestDataFactory.createActiveOutput();
        var expected = OrganizationTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(output);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOutputsValid_thenReturnResponses() {
        var output = OrganizationTestDataFactory.createActiveOutput();
        var expected = OrganizationTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(List.of(output));

        assertEquals(List.of(expected), result);
    }
}
