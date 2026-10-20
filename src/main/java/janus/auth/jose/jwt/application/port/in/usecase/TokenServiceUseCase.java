package janus.auth.jose.jwt.application.port.in.usecase;

import janus.auth.jose.jwt.domain.model.TokenIssuanceContext;

public interface TokenServiceUseCase {
    String issueGenericJwt(TokenIssuanceContext ctx);
}
