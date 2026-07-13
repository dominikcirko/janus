package io.github.janus.identity.credential.application.port.in.usecase;

import java.util.UUID;

public interface DeleteCredentialUseCase {

    void delete(UUID id, UUID userId);

}
