package io.github.janus.identity.adapter.out.persistence.mapper;

import io.github.janus.identity.adapter.out.persistence.entity.UserEntity;
import io.github.janus.identity.domain.model.User;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface UserPersistenceMapper {

    // mapper for Domain object -> entity
    UserEntity toEntity(UserEntity userEntity);
    User toDomain(Optional<UserEntity> userEntity);

}
