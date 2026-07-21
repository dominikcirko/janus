package io.github.janus.auth.jwt.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SigningAlgorithm {
    HS256(KeyType.SYMMETRIC),
    RS256(KeyType.ASYMMETRIC),
    ES256(KeyType.ASYMMETRIC);

    private final KeyType keyType;

}