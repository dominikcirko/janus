package janus.auth.jose.jwt.application.service;


import janus.auth.jose.jwt.domain.model.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final Environment env;

    private String issueJwt(JWTClaimsSet jwtClaimsSet){
    }

}
