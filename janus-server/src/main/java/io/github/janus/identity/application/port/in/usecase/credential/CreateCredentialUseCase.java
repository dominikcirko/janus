package io.github.janus.identity.application.port.in.usecase.credential;

import io.github.janus.identity.application.port.in.command.credential.CredentialCreateCommand;

public interface CreateCredentialUseCase {

    void create(CredentialCreateCommand command);

}
