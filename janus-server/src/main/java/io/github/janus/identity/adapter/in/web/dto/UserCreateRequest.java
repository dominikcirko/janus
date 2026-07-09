package io.github.janus.identity.adapter.in.web.dto;

import java.util.Set;
import java.util.UUID;

/*
* Represents create requests for users inside existing tenant
* */
public record UserCreateRequest(
        String username,
        String familyName,
        String givenName,
        String email,
        Set<UUID> roleIds,
        String password,
        boolean verified
    ) {
}
//take tenant id from endpoint?