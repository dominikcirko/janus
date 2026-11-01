package janus.identity.user.application.service;

import janus.common.exceptionhandler.exception.ForbiddenException;
import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.identity.user.application.port.out.PasswordCredentialRepository;
import janus.identity.user.application.port.out.UserRepository;
import janus.identity.user.application.port.usecase.model.PasswordCredential;
import janus.identity.user.application.port.usecase.model.User;
import janus.identity.user.testdata.UserTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static janus.identity.user.testdata.UserTestDataFactory.TEST_NEW_PASSWORD;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_NEW_PASSWORD_HASH;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_PASSWORD;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_PASSWORD_HASH;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordCredentialServiceTest {

    @Mock
    private PasswordCredentialRepository passwordCredentialRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private User userMock;

    private PasswordCredentialService passwordCredentialService;

    @BeforeEach
    void setUp() {
        passwordCredentialService = new PasswordCredentialService(
                passwordCredentialRepositoryMock,
                userRepositoryMock,
                passwordEncoderMock
        );
    }

    @Test
    void testWhenPasswordCredentialValid_thenHashAndSaveCredential() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.existsByUserId(TEST_USER_ID)).thenReturn(false);
        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(TEST_PASSWORD_HASH);

        passwordCredentialService.createPasswordCredential(TEST_USER_ID, TEST_PASSWORD, true);

        var credentialCaptor = ArgumentCaptor.forClass(PasswordCredential.class);
        verify(passwordCredentialRepositoryMock).create(credentialCaptor.capture());
        var savedCredential = credentialCaptor.getValue();

        assertEquals(TEST_USER_ID, savedCredential.getUserId());
        assertEquals(TEST_PASSWORD_HASH, savedCredential.getPasswordHash());
        assertNotNull(savedCredential.getPasswordChangedAt());
        assertTrue(savedCredential.isMustChangePassword());
    }

    @Test
    void testWhenUserIdNull_thenRejectCredentialCreation() {
        assertThrows(
                InvalidRequestException.class,
                () -> passwordCredentialService.createPasswordCredential(null, TEST_PASSWORD, false)
        );

        verifyNoInteractions(userRepositoryMock, passwordCredentialRepositoryMock, passwordEncoderMock);
    }

    @Test
    void testWhenPasswordBlank_thenRejectCredentialCreation() {
        assertThrows(
                InvalidRequestException.class,
                () -> passwordCredentialService.createPasswordCredential(TEST_USER_ID, " ", false)
        );

        verifyNoInteractions(userRepositoryMock, passwordCredentialRepositoryMock, passwordEncoderMock);
    }

    @Test
    void testWhenUserDoesNotExist_thenRejectCredentialCreation() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> passwordCredentialService.createPasswordCredential(TEST_USER_ID, TEST_PASSWORD, false)
        );

        verifyNoInteractions(passwordCredentialRepositoryMock, passwordEncoderMock);
    }

    @Test
    void testWhenPasswordCredentialAlreadyExists_thenRejectCreation() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.existsByUserId(TEST_USER_ID)).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> passwordCredentialService.createPasswordCredential(TEST_USER_ID, TEST_PASSWORD, false)
        );

        verifyNoInteractions(passwordEncoderMock);
    }

    @Test
    void testWhenPasswordMatches_thenReturnTrue() {
        var credential = UserTestDataFactory.createPasswordCredential();
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoderMock.matches(TEST_PASSWORD, TEST_PASSWORD_HASH)).thenReturn(true);

        var result = passwordCredentialService.verifyPassword(TEST_USER_ID, TEST_PASSWORD);

        assertTrue(result);
    }

    @Test
    void testWhenUserDoesNotExist_thenPasswordDoesNotMatch() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        var result = passwordCredentialService.verifyPassword(TEST_USER_ID, TEST_PASSWORD);

        assertFalse(result);
        verifyNoInteractions(passwordCredentialRepositoryMock, passwordEncoderMock);
    }

    @Test
    void testWhenCredentialDoesNotExist_thenPasswordDoesNotMatch() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        var result = passwordCredentialService.verifyPassword(TEST_USER_ID, TEST_PASSWORD);

        assertFalse(result);
        verifyNoInteractions(passwordEncoderMock);
    }

    @Test
    void testWhenPasswordDoesNotMatchHash_thenReturnFalse() {
        var credential = UserTestDataFactory.createPasswordCredential();
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoderMock.matches(TEST_PASSWORD, TEST_PASSWORD_HASH)).thenReturn(false);

        var result = passwordCredentialService.verifyPassword(TEST_USER_ID, TEST_PASSWORD);

        assertFalse(result);
    }

    @Test
    void testWhenPasswordChangeValid_thenHashAndSaveNewPassword() {
        var credential = UserTestDataFactory.createPasswordCredential();
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoderMock.matches(TEST_PASSWORD, TEST_PASSWORD_HASH)).thenReturn(true);
        when(passwordEncoderMock.encode(TEST_NEW_PASSWORD)).thenReturn(TEST_NEW_PASSWORD_HASH);

        passwordCredentialService.changePassword(TEST_USER_ID, TEST_PASSWORD, TEST_NEW_PASSWORD);

        var credentialCaptor = ArgumentCaptor.forClass(PasswordCredential.class);
        verify(passwordCredentialRepositoryMock).update(credentialCaptor.capture());
        var savedCredential = credentialCaptor.getValue();

        assertEquals(TEST_USER_ID, savedCredential.getUserId());
        assertEquals(TEST_NEW_PASSWORD_HASH, savedCredential.getPasswordHash());
        assertNotNull(savedCredential.getPasswordChangedAt());
        assertFalse(savedCredential.isMustChangePassword());
    }

    @Test
    void testWhenCurrentPasswordInvalid_thenRejectPasswordChange() {
        var credential = UserTestDataFactory.createPasswordCredential();
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(credential));
        when(passwordEncoderMock.matches(TEST_PASSWORD, TEST_PASSWORD_HASH)).thenReturn(false);

        assertThrows(
                ForbiddenException.class,
                () -> passwordCredentialService.changePassword(TEST_USER_ID, TEST_PASSWORD, TEST_NEW_PASSWORD)
        );

        verify(passwordEncoderMock, never()).encode(TEST_NEW_PASSWORD);
        verify(passwordCredentialRepositoryMock, never()).update(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void testWhenPasswordCredentialDoesNotExist_thenRejectPasswordChange() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(passwordCredentialRepositoryMock.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> passwordCredentialService.changePassword(TEST_USER_ID, TEST_PASSWORD, TEST_NEW_PASSWORD)
        );

        verifyNoInteractions(passwordEncoderMock);
    }

    @Test
    void testWhenUserExists_thenDeletePasswordCredential() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));

        passwordCredentialService.deletePasswordCredential(TEST_USER_ID);

        verify(passwordCredentialRepositoryMock).deleteByUserId(TEST_USER_ID);
    }
}
