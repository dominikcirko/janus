package io.github.janus.identity.application.mapper;

import io.github.janus.identity.application.port.in.command.credential.CredentialCreateCommand;
import io.github.janus.identity.application.port.in.command.credential.CredentialUpdateCommand;
import io.github.janus.identity.domain.model.Credential;
import org.mapstruct.Mapper;

@Mapper
public interface CredentialApplicationMapper {

    Credential toDomain(CredentialCreateCommand command);

    Credential toDomain(CredentialUpdateCommand command);
}
