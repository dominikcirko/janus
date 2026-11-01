package janus.identity.user.adapter.out.persistence.mapper;

import janus.identity.user.adapter.out.persistence.entity.UserEntity;
import janus.identity.user.application.port.usecase.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserPersistenceMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    UserEntity toEntity(User user);

    User toUser(UserEntity entity);
}
