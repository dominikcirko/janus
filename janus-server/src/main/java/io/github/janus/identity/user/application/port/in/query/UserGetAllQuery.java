package io.github.janus.identity.user.application.port.in.query;

import java.util.UUID;

//todo: add searchcriteria object when implemented
public record UserGetAllQuery(UUID tenantId) {
}
