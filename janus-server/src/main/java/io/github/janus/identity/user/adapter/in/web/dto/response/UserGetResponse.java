package io.github.janus.identity.user.adapter.in.web.dto.response;


import io.github.janus.identity.role.adapter.in.web.dto.RoleResponse;

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