package io.github.janus.identity.application.port.in.query.user;

import java.util.UUID;

//todo: add searchcriteria object when implemented
public record UserGetAllQuery(UUID tenantId) {
}
