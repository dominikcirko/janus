package janus.identity.user.adapter.out.persistence.mapper;

import janus.identity.user.adapter.out.persistence.entity.PasswordCredentialEntity;
import janus.identity.user.adapter.out.persistence.entity.UserEntity;
import janus.identity.user.application.port.usecase.model.PasswordCredential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PasswordCredentialPersistenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "passwordHash", source = "credential.passwordHash")
    @Mapping(target = "passwordChangedAt", source = "credential.passwordChangedAt")
    @Mapping(target = "mustChangePassword", source = "credential.mustChangePassword")
    PasswordCredentialEntity toEntity(PasswordCredential credential, UserEntity user);

    @Mapping(target = "userId", source = "user.id")
    PasswordCredential toCredential(PasswordCredentialEntity entity);
}
