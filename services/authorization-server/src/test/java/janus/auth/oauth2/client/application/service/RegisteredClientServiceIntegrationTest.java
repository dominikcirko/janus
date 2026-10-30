package janus.auth.oauth2.client.application.service;

import jakarta.persistence.EntityManager;
import janus.TestcontainersConfiguration;
import janus.auth.oauth2.client.adapter.out.persistence.JpaRegisteredClientRepository;
import janus.auth.oauth2.client.adapter.out.persistence.RegisteredClientRepositoryAdapter;
import janus.auth.oauth2.client.adapter.out.persistence.mapper.RegisteredClientPersistenceMapperImpl;
import janus.auth.oauth2.client.application.mapper.RegisteredClientMapperImpl;
import janus.auth.oauth2.client.testdata.RegisteredClientTestDataFactory;
import janus.config.JpaAuditingConfig;
import janus.config.PasswordEncodingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        RegisteredClientService.class,
        RegisteredClientRepositoryAdapter.class,
        RegisteredClientPersistenceMapperImpl.class,
        RegisteredClientMapperImpl.class,
        PasswordEncodingConfig.class,
        JpaAuditingConfig.class,
        TestcontainersConfiguration.class
})
public class RegisteredClientServiceIntegrationTest {

    @Autowired
    private RegisteredClientService registeredClientService;

    @Autowired
    private JpaRegisteredClientRepository registeredClientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testWhenRegistrationValid_thenPersistClient() {
        var input = RegisteredClientTestDataFactory
                .createValidConfidentialAuthorizationCodeInput();

        var result = registeredClientService.registerClient(input);
        entityManager.flush();
        entityManager.clear();

        var persistedClient = registeredClientRepository
                .findByClientId(result.clientId())
                .orElseThrow();

        assertEquals(input.clientName(), persistedClient.getClientName());
        assertEquals(input.redirectUris(), persistedClient.getRedirectUris());
        assertEquals(input.grantTypes(), persistedClient.getGrantTypes());
        assertEquals(input.responseTypes(), persistedClient.getResponseTypes());
        assertEquals(Set.of("openid", "profile"), persistedClient.getScopes());
        assertTrue(passwordEncoder.matches(
                result.clientSecret(),
                persistedClient.getClientSecretHash()
        ));
    }
}
