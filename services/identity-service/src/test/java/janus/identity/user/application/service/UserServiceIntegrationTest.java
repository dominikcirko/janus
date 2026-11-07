package janus.identity.user.application.service;

import jakarta.persistence.EntityManager;
import janus.TestcontainersConfiguration;
import janus.common.config.JpaAuditingConfig;
import janus.common.config.PasswordEncodingConfig;
import janus.identity.user.adapter.out.persistence.JpaPasswordCredentialRepository;
import janus.identity.user.adapter.out.persistence.JpaUserRepository;
import janus.identity.user.adapter.out.persistence.PasswordCredentialRepositoryAdapter;
import janus.identity.user.adapter.out.persistence.UserRepositoryAdapter;
import janus.identity.user.adapter.out.persistence.mapper.PasswordCredentialPersistenceMapperImpl;
import janus.identity.user.adapter.out.persistence.mapper.UserPersistenceMapperImpl;
import janus.identity.user.application.mapper.UserMapperImpl;
import janus.identity.user.domain.type.UserStatus;
import janus.identity.user.testdata.UserTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        UserService.class,
        PasswordCredentialService.class,
        UserRepositoryAdapter.class,
        PasswordCredentialRepositoryAdapter.class,
        UserPersistenceMapperImpl.class,
        PasswordCredentialPersistenceMapperImpl.class,
        UserMapperImpl.class,
        PasswordEncodingConfig.class,
        JpaAuditingConfig.class,
        TestcontainersConfiguration.class
})
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaPasswordCredentialRepository passwordCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testWhenUserInputValid_thenPersistUserAndPasswordCredential() {
        var input = UserTestDataFactory.createInputWithEmail(" User@Example.COM ");

        var result = userService.createUser(input);
        entityManager.flush();
        entityManager.clear();

        var persistedUser = userRepository.findById(result.id()).orElseThrow();
        var persistedCredential = passwordCredentialRepository.findById(result.id()).orElseThrow();

        assertEquals(UserTestDataFactory.TEST_EMAIL, persistedUser.getEmail());
        assertEquals(UserStatus.PENDING_ACTIVATION, persistedUser.getStatus());
        assertEquals(input.userMetadata(), persistedUser.getUserMetadata());
        assertTrue(passwordEncoder.matches(input.password(), persistedCredential.getPasswordHash()));
        assertFalse(persistedCredential.isMustChangePassword());
    }
}
