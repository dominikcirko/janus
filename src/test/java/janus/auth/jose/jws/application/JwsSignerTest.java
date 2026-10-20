package janus.auth.jose.jws.application;

import janus.auth.jose.common.JoseCommonParams;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.PublicJwk;
import janus.auth.jose.jws.domain.exception.JwsSigningException;
import janus.auth.jose.jws.domain.model.JwsHeader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwsSignerTest {


    @ParameterizedTest
    @MethodSource("invalidSigningInputs")
    void testWhenSigningInputsNull_thenThrow(JwsHeader header, PublicJwk jwk, byte[] payload,
                                             PrivateKey privateKey, String expectedMessage){
        JwsSigner signer = new JwsSigner(new ObjectMapper());

        var exception = assertThrows(
                JwsSigningException.class, () -> signer.sign(header, payload, privateKey, jwk)
        );
        assertEquals(expectedMessage, exception.getMessage());

    }

    private static Stream<Arguments> invalidSigningInputs(){
        JwsHeader header = mock(JwsHeader.class);
        JoseCommonParams params = mock(JoseCommonParams.class);
        PublicJwk jwk = mock(ECPublicJwk.class);
        byte[] payload = "test-payload".getBytes(StandardCharsets.UTF_8);
        PrivateKey privateKey = mock(PrivateKey.class);
        var expectedMessage = "Required JWS fields must not be null";

        when(header.params()).thenReturn(params);
        when(header.params().kid()).thenReturn(null);
        when(header.params()).thenReturn(null);
        when(header.alg()).thenReturn(null);
        when(jwk.kid()).thenReturn(null);
        when(jwk.alg()).thenReturn(null);

        return Stream.of(
            Arguments.of(null, jwk, payload, privateKey, expectedMessage),
            Arguments.of(header, null, payload, privateKey, expectedMessage),
            Arguments.of(header, jwk, null, privateKey, expectedMessage),
            Arguments.of(header, jwk, payload, null, expectedMessage)
        );
    }



}
