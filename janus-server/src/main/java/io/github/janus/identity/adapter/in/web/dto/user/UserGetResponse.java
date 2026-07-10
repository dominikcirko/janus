package io.github.janus.identity.adapter.in.web.dto.user;


import io.github.janus.identity.adapter.in.web.dto.role.RoleResponse;

import java.util.Set;

/*
 * Represents update requests for users inside existing tenant
 * */
public record UserGetResponse(String username,
                              String familyName,
                              String givenName,
                              String email,
                              Set<RoleResponse> roles,
                              boolean verified) {}
//take tenant id from endpoint?