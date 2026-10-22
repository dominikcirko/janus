package janus.auth.jose.jwt.application.service;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.common.JoseObjectType;
import janus.auth.jose.jwk.application.service.JwksService;
import janus.auth.jose.jwk.application.service.Pkcs12SigningKeyStore;
import janus.auth.jose.jwk.domain.model.ECPublicJwk;
import janus.auth.jose.jws.application.JwsSigner;
import janus.auth.jose.jws.domain.model.JwsHeader;
import janus.auth.jose.jwt.domain.model.TokenIssuanceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.security.PrivateKey;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    public static final String TEST_ISSUER = "test_issuer";
    public static final String TEST_SUBJECT = "test_subject";
    public static final String TEST_AUDIENCE = "test_audience";
    public static final String TEST_KID = "test_kid";
    public static final String TEST_SIGNED_JWT = "header.payload.signature";
    public static final Duration TEST_TOKEN_LIFETIME = Duration.ofMinutes(5);

    @Mock
    private JwsSigner jwsSignerMock;
    @Mock
    private Pkcs12SigningKeyStore keyStoreMock;
    @Mock
    private JwksService jwksServiceMock;
    @Mock
    private ECPublicJwk publicJwkMock;
    @Mock
    private PrivateKey privateKeyMock;

    private ObjectMapper objectMapper;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        tokenService = new TokenService(
                objectMapper,
                jwsSignerMock,
                keyStoreMock,
                jwksServiceMock
        );
    }

    @Test
    void testWhenTokenIssuanceContextValid_thenReturnSignedJwt() {
        var context = new TokenIssuanceContext(
                TEST_ISSUER,
                TEST_SUBJECT,
                TEST_AUDIENCE,
                TEST_TOKEN_LIFETIME,
                Map.of("department", "marketing")
        );
        when(jwksServiceMock.getActivePublicSigningJwk(Algorithm.ES256))
                .thenReturn(publicJwkMock);
        when(publicJwkMock.kid()).thenReturn(TEST_KID);
        when(publicJwkMock.alg()).thenReturn(Algorithm.ES256);
        when(keyStoreMock.getPrivateKey(TEST_KID)).thenReturn(privateKeyMock);
        when(jwsSignerMock.sign(
                any(JwsHeader.class),
                any(byte[].class),
                same(privateKeyMock),
                same(publicJwkMock)
        )).thenReturn(TEST_SIGNED_JWT);

        var result = tokenService.issueGenericJwt(context);

        var headerCaptor = ArgumentCaptor.forClass(JwsHeader.class);
        var payloadCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(jwsSignerMock).sign(
                headerCaptor.capture(),
                payloadCaptor.capture(),
                same(privateKeyMock),
                same(publicJwkMock)
        );
        var header = headerCaptor.getValue();
        var payload = objectMapper.readTree(payloadCaptor.getValue());

        assertEquals(TEST_SIGNED_JWT, result);
        assertEquals(Algorithm.ES256, header.alg());
        assertEquals(TEST_KID, header.params().kid());
        assertEquals(JoseObjectType.JWT, header.params().typ());
        assertEquals(TEST_ISSUER, payload.get("iss").asString());
        assertEquals(TEST_SUBJECT, payload.get("sub").asString());
        assertEquals(TEST_AUDIENCE, payload.get("aud").asString());
        assertEquals("marketing", payload.get("department").asString());
        verify(jwksServiceMock).getActivePublicSigningJwk(Algorithm.ES256);
        verify(keyStoreMock).getPrivateKey(TEST_KID);
    }
}
