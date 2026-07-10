package io.github.janus.identity.credential.application.port.in.usecase;

import io.github.janus.identity.credential.application.port.in.command.CredentialCreateCommand;
import io.github.janus.identity.credential.domain.model.Credential;

public interface CreateCredentialUseCase {

    Credential create(CredentialCreateCommand command);

}
