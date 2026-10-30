package janus.auth.jose.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Algorithm {
    RS256(KeyType.ASYMMETRIC),
    ES256(KeyType.ASYMMETRIC),
    ES384(KeyType.ASYMMETRIC),
    ES512(KeyType.ASYMMETRIC);

    private final KeyType keyType;
}
