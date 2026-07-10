package io.github.janus.identity.application.mapper;

import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.user.UserUpdateCommand;
import io.github.janus.identity.domain.model.Credential;
import io.github.janus.identity.domain.model.Role;
import io.github.janus.identity.domain.model.User;
import io.github.janus.tenant.Tenant;
import org.mapstruct.Mapper;

@Mapper
public interface UserApplicationMapper {

    User toDomain(UserCreateCommand command);

    User toDomain(UserUpdateCommand command);

    Credential toDomain(CreateCredentialCommand command);

    Credential toDomain(UpdateCredentialCommand command);

    Role toDomain(CreateRoleCommand command);

    Role toDomain(UpdateRoleCommand command);

    Tenant toDomain(CreateTenantCommand command);

    Tenant toDomain(UpdateTenantCommand command);

}
