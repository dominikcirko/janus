package io.github.janus.auth.jwt.domain.model;

import io.github.janus.auth.jwt.domain.type.PublicKeyUse;

//https://datatracker.ietf.org/doc/html/rfc7517#page-4
public record JsonWebKey(String kty,
                         PublicKeyUse use,
                         ) {
}
