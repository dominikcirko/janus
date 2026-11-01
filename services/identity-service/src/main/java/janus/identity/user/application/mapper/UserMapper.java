package janus.identity.user.application.mapper;

import janus.identity.user.application.port.usecase.model.User;
import janus.identity.user.application.port.usecase.model.UserCreateInput;
import janus.identity.user.application.port.usecase.model.UserOutput;
import janus.identity.user.application.port.usecase.model.UserUpdateInput;
import janus.identity.user.domain.type.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "normalizedEmail")
    @Mapping(target = "status", constant = "PENDING_ACTIVATION")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "systemMetadata", expression = "java(java.util.Map.of())")
    User toUser(UserCreateInput input, String normalizedEmail);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "emailVerified", source = "user.emailVerified")
    @Mapping(target = "displayName", source = "input.displayName")
    @Mapping(target = "givenName", source = "input.givenName")
    @Mapping(target = "familyName", source = "input.familyName")
    @Mapping(target = "pictureUri", source = "input.pictureUri")
    @Mapping(target = "userMetadata", source = "input.userMetadata")
    @Mapping(target = "systemMetadata", source = "user.systemMetadata")
    User toUpdatedUser(User user, UserUpdateInput input);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "emailVerified", source = "user.emailVerified")
    @Mapping(target = "displayName", source = "user.displayName")
    @Mapping(target = "givenName", source = "user.givenName")
    @Mapping(target = "familyName", source = "user.familyName")
    @Mapping(target = "pictureUri", source = "user.pictureUri")
    @Mapping(target = "userMetadata", source = "user.userMetadata")
    @Mapping(target = "systemMetadata", source = "user.systemMetadata")
    User withUpdatedStatus(User user, UserStatus status);

    UserOutput toOutput(User user);
}
