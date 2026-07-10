package io.github.janus.identity.application.service;

import io.github.janus.identity.application.mapper.CredentialApplicationMapper;
import io.github.janus.identity.application.port.in.command.credential.CredentialCreateCommand;
import io.github.janus.identity.application.port.in.command.credential.CredentialUpdateCommand;
import io.github.janus.identity.application.port.in.query.credential.CredentialGetByIdQuery;
import io.github.janus.identity.application.port.in.usecase.credential.CreateCredentialUseCase;
import io.github.janus.identity.application.port.in.usecase.credential.DeleteCredentialUseCase;
import io.github.janus.identity.application.port.in.usecase.credential.GetCredentialUseCase;
import io.github.janus.identity.application.port.in.usecase.credential.UpdateCredentialUseCase;
import io.github.janus.identity.application.port.out.CredentialRepository;
import io.github.janus.identity.domain.model.Credential;
import io.github.janus.identity.domain.type.CredentialType;
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
    public void create(CredentialCreateCommand command) {
        var credential = mapper.toDomain(command);
        if (command.type() == CredentialType.PASSWORD) {
            credential.hashPassword(command.type(), encoder.encode(command.value()));
        }
        credentialRepository.save(credential);
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
            credential.hashPassword(command.type(), encoder.encode(command.value()));
        }
        credentialRepository.update(credential);
    }
}
