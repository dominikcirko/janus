package io.github.janus.identity.application.port.in.usecase.credential;

import io.github.janus.identity.application.port.in.command.credential.CredentialCreateCommand;
import io.github.janus.identity.domain.model.Credential;

public interface CreateCredentialUseCase {

    Credential create(CredentialCreateCommand command);

}
