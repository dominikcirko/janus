package io.github.janus.identity.adapter.in.web.dto;


import java.util.Set;

/*
 * Represents update requests for users inside existing tenant
 * */
public record UserResponse(
        String username,
        String familyName,
        String givenName,
        String email,
        Set<RoleResponse> roles
) {
}
//take tenant id from endpoint?