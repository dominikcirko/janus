package io.github.janus.identity.credential.adapter.out.persistence;

import io.github.janus.identity.credential.domain.model.Credential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CredentialPersistenceMapper {

    @Mapping(target = "userId", source = "user.id")
    Credential toDomain(CredentialEntity credentialEntity);

    @Mapping(target = "user", ignore = true)
    CredentialEntity toEntity(Credential credential);

    List<Credential> toDomainList(List<CredentialEntity> credentials);
    List<CredentialEntity> toEntityList(List<Credential> credentials);
}
