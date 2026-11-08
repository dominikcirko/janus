package janus.auth.config;

import janus.auth.oauth2.client.adapter.out.persistence.HibernateClientTenantLookup;
import janus.common.TenantIdentifierResolver;
import janus.common.config.ApiVersioningConfig;
import janus.common.config.JpaAuditingConfig;
import janus.common.config.PasswordEncodingConfig;
import janus.common.config.TokenVerifierConfig;
import org.hibernate.cfg.MultiTenancySettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({ApiVersioningConfig.class, JpaAuditingConfig.class, TokenVerifierConfig.class, PasswordEncodingConfig.class})
public class SharedInfrastructureConfig {

    @Bean
    private TenantIdentifierResolver tenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }

    @Bean
    private HibernatePropertiesCustomizer tenantHibernateProperties(
            HibernateClientTenantLookup tenantIdentifierResolver) {
        return properties -> properties.put(
                MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver
        );
    }
}
