package io.github.janus.tenancy.tenant.adapter.out.persistence;

import io.github.janus.tenancy.tenant.domain.model.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TenantPersistenceMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customer", ignore = true)
    TenantEntity toEntity(Tenant tenant);

    @Mapping(target = "customerId", source = "customer.id")
    Tenant toDomain(TenantEntity entity);

}
