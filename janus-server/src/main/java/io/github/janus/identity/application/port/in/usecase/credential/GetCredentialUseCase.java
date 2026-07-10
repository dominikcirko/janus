package io.github.janus.identity.application.port.in.usecase.credential;

import io.github.janus.identity.application.port.in.query.credential.CredentialGetByIdQuery;
import io.github.janus.identity.domain.model.Credential;

import java.util.List;
import java.util.UUID;

public interface GetCredentialUseCase {

    Credential getCredentialById(CredentialGetByIdQuery query);

    List<Credential> getCredentials(UUID userId);

}
