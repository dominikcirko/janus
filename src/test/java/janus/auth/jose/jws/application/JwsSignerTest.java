package janus.auth.jose.jws.application;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.common.JoseCommonParams;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jwk.domain.model.PublicJwk;
import janus.auth.jose.jws.domain.exception.JwsSigningException;
import janus.auth.jose.jws.domain.model.JwsHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwsSignerTest {

    @Mock
    private JwsHeader header;
    @Mock
    private JoseCommonParams params;
    @Mock
    private ECPublicJwk jwk;
    @Mock
    private PrivateKey privateKey;

    private static final byte[] TEST_PAYLOAD =
            "test-payload".getBytes(StandardCharsets.UTF_8);

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
        PrivateKey privateKey = mock(PrivateKey.class);
        var expectedMessage = "Required JWS fields must not be null";

        when(header.params()).thenReturn(params);
        when(header.params().kid()).thenReturn(null);
        when(header.params()).thenReturn(null);
        when(header.alg()).thenReturn(null);
        when(jwk.kid()).thenReturn(null);
        when(jwk.alg()).thenReturn(null);

        return Stream.of(
            Arguments.of(null, jwk, TEST_PAYLOAD, privateKey, expectedMessage),
            Arguments.of(header, null, TEST_PAYLOAD, privateKey, expectedMessage),
            Arguments.of(header, jwk, null, privateKey, expectedMessage),
            Arguments.of(header, jwk, TEST_PAYLOAD, null, expectedMessage)
        );
    }

    @Test
    void testWhenHeaderKidNotEqualToJwkKid_thenThrow(){

        when(header.params()).thenReturn(params);
        when(header.alg()).thenReturn(Algorithm.ES256);
        when(header.params().kid()).thenReturn("kid1");
        when(jwk.kid()).thenReturn("kid2");
        when(jwk.alg()).thenReturn(Algorithm.ES256);

        var expectedMessage = "JWS Header's kid must be identical to public JWK's kid";

        JwsSigner signer = new JwsSigner(new ObjectMapper());

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(header, TEST_PAYLOAD, privateKey, jwk));
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void testWhenHeaderAlgNotEqualToJwkAlg_thenThrow(){

        when(header.params()).thenReturn(params);
        when(header.params().kid()).thenReturn("kid1");
        when(jwk.kid()).thenReturn("kid1");
        when(jwk.alg()).thenReturn(Algorithm.ES384);
        when(header.alg()).thenReturn(Algorithm.ES256);

        var expectedMessage = "JWS Header's alg must be identical to public JWK's alg";

        JwsSigner signer = new JwsSigner(new ObjectMapper());

        var exception = assertThrows(
                JwsSigningException.class,
                () -> signer.sign(header, TEST_PAYLOAD, privateKey, jwk));
        assertEquals(expectedMessage, exception.getMessage());

    }

}
