package janus.common.config;

import janus.common.JwksKeyProvider;
import janus.common.security.token.AccessTokenVerifier;
import janus.common.security.token.TenantIssuerClient;
import janus.common.filters.AccessTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TokenVerifierProperties.class)
public class TokenVerifierConfig {

    @Bean
    private JwksKeyProvider jwksKeyProvider(RestClient.Builder builder,
                                            @Value("${janus.jose.jwks-uri}") URI jwksUri) {
        return new JwksKeyProvider(builder.build(), jwksUri);
    }

    @Bean
    private TenantIssuerClient tenantIssuerClient(RestClient.Builder builder,
                                                  TokenVerifierProperties properties) {
        RestClient restClient = builder
                .baseUrl(properties.tenantIssuerServiceUrl().toString())
                .defaultHeaders(headers -> headers.setBearerAuth(properties.tenantIssuerServiceToken()))
                .build();
        return new TenantIssuerClient(restClient);
    }

    @Bean
    private  AccessTokenVerifier accessTokenVerifier(
            ObjectMapper objectMapper,
            JwksKeyProvider jwksKeyProvider,
            TenantIssuerClient tenantIssuerClient,
            TokenVerifierProperties properties,
            @Value("${janus.security.clock-skew-seconds:30}") long clockSkewSeconds
    ) {
        return new AccessTokenVerifier(objectMapper, clockSkewSeconds, jwksKeyProvider,
                properties.audience(), tenantIssuerClient);
    }

    @Bean
    private FilterRegistrationBean<AccessTokenAuthenticationFilter> accessTokenAuthenticationFilter(
            AccessTokenVerifier accessTokenVerifier,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
            TokenVerifierProperties properties
    ) {
        var filter = new AccessTokenAuthenticationFilter(accessTokenVerifier, exceptionResolver);
        var registration = new FilterRegistrationBean<>(filter);
        registration.setUrlPatterns(properties.protectedPaths());
        registration.setOrder(0);
        return registration;
    }
}
