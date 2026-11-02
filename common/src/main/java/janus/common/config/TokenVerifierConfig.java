package janus.common.config;

import janus.common.security.token.AccessTokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class TokenVerifierConfig {

    @Bean
    AccessTokenVerifier accessTokenVerifier(
            ObjectMapper objectMapper,
            @Value("${janus.security.clock-skew-seconds:30}") long clockSkewSeconds
    ) {
        return new AccessTokenVerifier(objectMapper, clockSkewSeconds);
    }
}