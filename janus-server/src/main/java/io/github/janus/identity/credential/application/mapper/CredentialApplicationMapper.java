package io.github.janus.identity.credential.application.mapper;

import io.github.janus.identity.credential.application.port.in.command.CredentialCreateCommand;
import io.github.janus.identity.credential.application.port.in.command.CredentialUpdateCommand;
import io.github.janus.identity.credential.domain.model.Credential;
import org.mapstruct.Mapper;

@Mapper
public interface CredentialApplicationMapper {

    Credential toDomain(CredentialCreateCommand command);

    Credential toDomain(CredentialUpdateCommand command);
}
