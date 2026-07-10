package io.github.janus.identity.credential.application.service;

import io.github.janus.identity.credential.application.mapper.CredentialApplicationMapper;
import io.github.janus.identity.credential.application.port.in.command.CredentialCreateCommand;
import io.github.janus.identity.credential.application.port.in.command.CredentialUpdateCommand;
import io.github.janus.identity.credential.application.port.in.query.CredentialGetByIdQuery;
import io.github.janus.identity.credential.application.port.in.usecase.CreateCredentialUseCase;
import io.github.janus.identity.credential.application.port.in.usecase.DeleteCredentialUseCase;
import io.github.janus.identity.credential.application.port.in.usecase.GetCredentialUseCase;
import io.github.janus.identity.credential.application.port.in.usecase.UpdateCredentialUseCase;
import io.github.janus.identity.credential.application.port.out.CredentialRepository;
import io.github.janus.identity.credential.domain.model.Credential;
import io.github.janus.identity.credential.domain.type.CredentialType;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CredentialService implements CreateCredentialUseCase,
        DeleteCredentialUseCase, GetCredentialUseCase, UpdateCredentialUseCase {

    private final Argon2PasswordEncoder encoder;
    private final CredentialApplicationMapper mapper;
    private final CredentialRepository credentialRepository;

    public CredentialService(Argon2PasswordEncoder encoder,
                             CredentialApplicationMapper mapper,
                             CredentialRepository credentialRepository) {
        this.encoder = encoder;
        this.mapper = mapper;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public Credential create(CredentialCreateCommand command) {
        var credential = mapper.toDomain(command);
        if (command.type() == CredentialType.PASSWORD) {
            credential.setPasswordHash(command.type(), encoder.encode(command.value()));
        }
        // todo: cover other credential types
        return credentialRepository.save(credential);
    }

    @Override
    public void delete(UUID id, UUID userId) {
        credentialRepository.delete(id, userId);
    }

    @Override
    public Credential getCredentialById(CredentialGetByIdQuery query) {
        return credentialRepository.findById(query.id(), query.userId());
    }

    @Override
    public List<Credential> getCredentials(UUID userId) {
        return credentialRepository.findAllByUserId(userId);
    }

    @Override
    public void update(CredentialUpdateCommand command) {
        var credential = mapper.toDomain(command);
        if (command.type() == CredentialType.PASSWORD) {
            credential.setPasswordHash(command.type(), encoder.encode(command.value()));
        }
        // todo: cover other credential types
        credentialRepository.update(credential);
    }
}
