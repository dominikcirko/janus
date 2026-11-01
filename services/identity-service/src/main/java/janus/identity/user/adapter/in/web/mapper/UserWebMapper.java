package janus.identity.user.adapter.in.web.mapper;

import janus.identity.user.adapter.in.web.dto.UserRequest;
import janus.identity.user.adapter.in.web.dto.UserResponse;
import janus.identity.user.adapter.in.web.dto.UserUpdateRequest;
import janus.identity.user.application.port.usecase.model.UserCreateInput;
import janus.identity.user.application.port.usecase.model.UserOutput;
import janus.identity.user.application.port.usecase.model.UserUpdateInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserWebMapper {

    UserCreateInput toCreateInput(UserRequest request);
    UserUpdateInput toUpdateInput(UserUpdateRequest request);
    UserResponse toResponse(UserOutput output);
    List<UserResponse> toResponse(List<UserOutput> output);
}
