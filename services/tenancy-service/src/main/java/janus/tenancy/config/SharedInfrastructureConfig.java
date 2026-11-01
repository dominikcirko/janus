package janus.tenancy.config;

import janus.common.config.ApiVersioningConfig;
import janus.common.config.JpaAuditingConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApiVersioningConfig.class, JpaAuditingConfig.class})
public class SharedInfrastructureConfig {
}
