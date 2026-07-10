package io.github.janus.identity.credential.application.port.in.usecase;

import io.github.janus.identity.credential.application.port.in.query.CredentialGetByIdQuery;
import io.github.janus.identity.credential.domain.model.Credential;

import java.util.List;
import java.util.UUID;

public interface GetCredentialUseCase {

    Credential getCredentialById(CredentialGetByIdQuery query);

    List<Credential> getCredentials(UUID userId);

}
