package io.github.janus.identity.application.port.in.usecase.credential;

import java.util.UUID;

public interface DeleteCredentialUseCase {

    void delete(UUID id, UUID userId);

}
