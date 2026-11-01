package janus.identity.user.adapter.in.web.mapper;

import janus.identity.user.testdata.UserTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserWebMapperTest {

    private final UserWebMapper mapper = Mappers.getMapper(UserWebMapper.class);

    @Test
    void testWhenUserRequestValid_thenReturnCreateInput() {
        var request = UserTestDataFactory.createValidRequest();
        var expected = UserTestDataFactory.createValidInput();

        var result = mapper.toCreateInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenUserUpdateRequestValid_thenReturnUpdateInput() {
        var request = UserTestDataFactory.createValidUpdateRequest();
        var expected = UserTestDataFactory.createValidUpdateInput();

        var result = mapper.toUpdateInput(request);

        assertEquals(expected, result);
    }

    @Test
    void testWhenUserOutputValid_thenReturnResponse() {
        var output = UserTestDataFactory.createActiveOutput();
        var expected = UserTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(output);

        assertEquals(expected, result);
    }

    @Test
    void testWhenUserOutputsValid_thenReturnResponses() {
        var output = UserTestDataFactory.createActiveOutput();
        var expected = UserTestDataFactory.createActiveResponse();

        var result = mapper.toResponse(List.of(output));

        assertEquals(List.of(expected), result);
    }
}
