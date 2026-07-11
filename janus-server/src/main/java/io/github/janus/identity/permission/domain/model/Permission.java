package io.github.janus.identity.permission.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Permission {
    private UUID id;
    private String name;
}
