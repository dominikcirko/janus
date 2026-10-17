package janus.auth.jose.jwt.application.service;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.common.JoseCommonParams;
import janus.auth.jose.common.JoseObjectType;
import janus.auth.jose.jwk.application.service.JwksService;
import janus.auth.jose.jwk.application.service.Pkcs12SigningKeyStore;
import janus.auth.jose.jws.application.JwsSigner;
import janus.auth.jose.jws.domain.model.JwsHeader;
import janus.auth.jose.jwt.domain.model.JWTClaimsSet;
import janus.auth.jose.jwt.domain.model.TokenIssuanceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@Service
@RequiredArgsConstructor
public class TokenService  {

    private final ObjectMapper objectMapper;
    private final JwsSigner jwsSigner;
    private final Pkcs12SigningKeyStore keyStore;
    private final JwksService jwksService;

    public String issueJwt(TokenIssuanceContext ctx) throws
            NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        var jwtClaimsSet = new JWTClaimsSet(ctx);
        var jwtClaimsSetBytes = objectMapper.writeValueAsBytes(jwtClaimsSet);
        var publicJwk = jwksService.getActivePublicSigningJwk(Algorithm.ES256);

        JoseCommonParams params = JoseCommonParams.builder()
                .kid(publicJwk.kid())
                .typ(JoseObjectType.JWT_ACCESS_TOKEN)
                .build();

        //we are using ES256 as default algorithm, todo: later make it configurable
        JwsHeader jwsHeader = JwsHeader.builder()
                .alg(publicJwk.alg())
                .params(params)
                .build();

        // only jws for now, todo: jwe later
        return jwsSigner.sign(
                jwsHeader,
                jwtClaimsSetBytes,
                keyStore.getPrivateKey(publicJwk.kid()),
                publicJwk);
    }

}
