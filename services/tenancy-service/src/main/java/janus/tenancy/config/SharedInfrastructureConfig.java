package janus.tenancy.config;

import janus.common.config.ApiVersioningConfig;
import janus.common.config.JpaAuditingConfig;
import janus.common.config.TokenVerifierConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({ApiVersioningConfig.class, JpaAuditingConfig.class, TokenVerifierConfig.class})
public class SharedInfrastructureConfig {
}
