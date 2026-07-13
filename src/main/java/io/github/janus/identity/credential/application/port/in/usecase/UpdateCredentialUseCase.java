package io.github.janus.identity.credential.application.port.in.usecase;

import io.github.janus.identity.credential.application.port.in.command.CredentialUpdateCommand;

public interface UpdateCredentialUseCase {

    void update(CredentialUpdateCommand command);

}
