package io.github.janus.identity.application.port.in.usecase.credential;

import io.github.janus.identity.application.port.in.command.credential.CredentialUpdateCommand;

public interface UpdateCredentialUseCase {

    void update(CredentialUpdateCommand command);

}
