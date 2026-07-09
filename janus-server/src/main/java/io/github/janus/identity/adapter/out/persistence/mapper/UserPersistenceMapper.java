package io.github.janus.identity.adapter.out.persistence.mapper;

import io.github.janus.identity.adapter.out.persistence.entity.UserEntity;
import io.github.janus.identity.domain.model.User;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserPersistenceMapper {

    // mapper for Domain object -> entity
    UserEntity toEntity(User user);
    User toDomain(Optional<UserEntity> userEntity);
    List<User> toDomainList(List<UserEntity> users);
    List<UserEntity> toEntityList(List<User> users);

}
