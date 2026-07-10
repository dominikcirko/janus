package io.github.janus.identity.adapter.in.web.dto.user.response;

import io.github.janus.identity.adapter.in.web.dto.role.RoleResponse;

import java.util.Set;

public record UserUpdateResponse(String username,
                              String familyName,
                              String givenName,
                              String email,
                              Set<RoleResponse> roles,
                              boolean verified) {}
