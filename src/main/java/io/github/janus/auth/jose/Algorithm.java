package io.github.janus.auth.jose;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Algorithm {
    HS256(KeyType.SYMMETRIC),
    RS256(KeyType.ASYMMETRIC),
    ES256(KeyType.ASYMMETRIC),
    ES384(KeyType.ASYMMETRIC),
    ES512(KeyType.ASYMMETRIC);

    private final KeyType keyType;
}
