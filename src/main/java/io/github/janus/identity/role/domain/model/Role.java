package io.github.janus.identity.role.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private UUID id;
    private String name;
    private UUID tenantId;
    private String description;
    private Set<UUID> permissions;
}
