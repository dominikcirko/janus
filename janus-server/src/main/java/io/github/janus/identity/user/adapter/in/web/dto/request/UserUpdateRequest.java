package io.github.janus.identity.user.adapter.in.web.dto.request;

import java.util.Set;
import java.util.UUID;

/*
 * Represents update requests for users inside existing tenant
 * */
public record UserUpdateRequest(String username,
                                String familyName,
                                String givenName,
                                String email,
                                Set<UUID> roleIds,
                                String password,
                                boolean verified) {}
//take tenant id from endpoint?
