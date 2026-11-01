package janus.tenancy.organization.adapter.in.web.mapper;

import janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrganizationMembershipWebMapperTest {

    private final OrganizationMembershipWebMapper mapper = Mappers.getMapper(OrganizationMembershipWebMapper.class);

    @Test
    void testWhenOrganizationMembershipRequestValid_thenReturnInput() {
        var request = OrganizationMembershipTestDataFactory.createValidRequest();
        var expected = OrganizationMembershipTestDataFactory.createValidInput();

        var result = mapper.toInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOrganizationMembershipUpdateRequestValid_thenReturnUpdateInput() {
        var request = OrganizationMembershipTestDataFactory.createValidUpdateRequest();
        var expected = OrganizationMembershipTestDataFactory.createValidUpdateInput();

        var result = mapper.toInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOrganizationMembershipOutputValid_thenReturnResponse() {
        var output = OrganizationMembershipTestDataFactory.createOutput();
        var expected = OrganizationMembershipTestDataFactory.createResponse();

        var result = mapper.toResponse(output);

        assertEquals(expected, result);
    }

    @Test
    void testWhenOrganizationMembershipOutputsValid_thenReturnResponses() {
        var output = OrganizationMembershipTestDataFactory.createOutput();
        var expected = OrganizationMembershipTestDataFactory.createResponse();

        var result = mapper.toResponse(List.of(output));

        assertEquals(List.of(expected), result);
    }
}
