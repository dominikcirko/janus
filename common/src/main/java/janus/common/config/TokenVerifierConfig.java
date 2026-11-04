package janus.common.config;

import janus.common.JwksKeyProvider;
import janus.common.security.token.AccessTokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(TokenVerifierProperties.class)
public class TokenVerifierConfig {

    @Bean
    JwksKeyProvider jwksKeyProvider(
            RestClient.Builder builder,
            @Value("${janus.jose.jwks-uri}") URI jwksUri
    ) {
        return new JwksKeyProvider(builder.build(), jwksUri);
    }

    @Bean
    AccessTokenVerifier accessTokenVerifier(
            ObjectMapper objectMapper,
            JwksKeyProvider jwksKeyProvider,
            TokenVerifierProperties properties,
            @Value("${janus.security.clock-skew-seconds:30}") long clockSkewSeconds
    ) {
        return new AccessTokenVerifier(objectMapper, clockSkewSeconds, jwksKeyProvider,
                properties.audience());
    }
}
