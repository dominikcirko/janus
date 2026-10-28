package janus.auth.oauth2.client.adapter.out.persistence.mapper;

import janus.auth.oauth2.client.adapter.out.persistence.entity.RegisteredClientEntity;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RegisteredClientPersistenceMapper {

    RegisteredClientEntity toEntity(RegisteredClient client);

    RegisteredClient toClient(RegisteredClientEntity entity);
}
