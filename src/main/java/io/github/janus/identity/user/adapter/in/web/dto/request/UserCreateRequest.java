package io.github.janus.identity.user.adapter.in.web.dto.request;

import io.github.janus.identity.credential.domain.type.CredentialType;

import java.util.Set;
import java.util.UUID;

/*
* Represents create requests for users inside existing tenant
* */
public record UserCreateRequest(String username,
                                String familyName,
                                String givenName,
                                String email,
                                Set<UUID> roleIds,
                                String credentialValue,
                                CredentialType type,
                                boolean verified) {}