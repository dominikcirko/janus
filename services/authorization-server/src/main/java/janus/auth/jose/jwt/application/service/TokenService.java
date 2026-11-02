package janus.auth.jose.jwt.application.service;

import janus.auth.jose.common.Algorithm;
import janus.auth.jose.common.JoseCommonParams;
import janus.auth.jose.common.JoseObjectType;
import janus.auth.jose.jwk.application.service.JwksService;
import janus.auth.jose.jwk.application.service.Pkcs12SigningKeyStore;
import janus.auth.jose.jws.application.JwsSigner;
import janus.auth.jose.jws.domain.model.JwsHeader;
import janus.auth.jose.jwt.application.port.in.usecase.TokenServiceUseCase;
import janus.auth.jose.jwt.domain.model.JWTClaimsSet;
import janus.auth.jose.jwt.domain.model.TokenIssuanceContext;
import janus.common.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenService implements TokenServiceUseCase {

    private final ObjectMapper objectMapper;
    private final JwsSigner jwsSigner;
    private final Pkcs12SigningKeyStore keyStore;
    private final JwksService jwksService;

    public String issueGenericJwt(TokenIssuanceContext ctx) {

        var jwtClaimsSet = new JWTClaimsSet(ctx);
        var jwtClaimsSetBytes = objectMapper.writeValueAsBytes(jwtClaimsSet);
        var publicJwk = jwksService.getActivePublicSigningJwk(Algorithm.ES256);

        JoseCommonParams params = JoseCommonParams.builder()
                .kid(publicJwk.kid())
                .typ(JoseObjectType.JWT)
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

    public String issueAccessJwt(TokenIssuanceContext ctx, String clientId, Set<String> grantedScopes) {
        var tenantId = TenantContext.currentTenantId();

        var customClaims = new HashMap<>(ctx.customClaims());
        customClaims.put("tenant_id", tenantId.toString());
        customClaims.put("client_id", clientId);
        customClaims.put("scope", String.join(" ", grantedScopes));

        var accessTokenContext = new TokenIssuanceContext(
                ctx.issuer(),
                ctx.subject(),
                ctx.audience(),
                ctx.tokenLifetime(),
                customClaims
        );

        var jwtClaimsSet = new JWTClaimsSet(accessTokenContext);
        var jwtClaimsSetBytes = objectMapper.writeValueAsBytes(jwtClaimsSet);
        var publicJwk = jwksService.getActivePublicSigningJwk(Algorithm.ES256);

        JoseCommonParams params = JoseCommonParams.builder()
                .kid(publicJwk.kid())
                .typ(JoseObjectType.JWT_ACCESS_TOKEN)
                .build();

        JwsHeader jwsHeader = JwsHeader.builder()
                .alg(publicJwk.alg())
                .params(params)
                .build();

        return jwsSigner.sign(
                jwsHeader,
                jwtClaimsSetBytes,
                keyStore.getPrivateKey(publicJwk.kid()),
                publicJwk);
    }

}
