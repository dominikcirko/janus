package janus.identity.user.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.identity.user.application.mapper.UserMapper;
import janus.identity.user.application.port.out.UserRepository;
import janus.identity.user.application.port.usecase.PasswordCredentialUseCase;
import janus.identity.user.application.port.usecase.model.User;
import janus.identity.user.application.port.usecase.model.UserCreateInput;
import janus.identity.user.application.port.usecase.model.UserOutput;
import janus.identity.user.domain.type.UserStatus;
import janus.identity.user.testdata.UserTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static janus.identity.user.testdata.UserTestDataFactory.TEST_EMAIL;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_PASSWORD;
import static janus.identity.user.testdata.UserTestDataFactory.TEST_USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private PasswordCredentialUseCase passwordCredentialUseCaseMock;
    @Mock
    private UserMapper userMapperMock;
    @Mock
    private User userMock;
    @Mock
    private User mappedUserMock;
    @Mock
    private User savedUserMock;
    @Mock
    private UserOutput outputMock;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepositoryMock, passwordCredentialUseCaseMock, userMapperMock);
    }

    @Test
    void testWhenUserInputValid_thenSaveUserAndPasswordCredential() {
        var input = UserTestDataFactory.createInputWithEmail(" User@Example.COM ");
        when(userRepositoryMock.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userMapperMock.toUser(input, TEST_EMAIL)).thenReturn(mappedUserMock);
        when(userRepositoryMock.save(mappedUserMock)).thenReturn(savedUserMock);
        when(savedUserMock.getId()).thenReturn(TEST_USER_ID);
        when(userMapperMock.toOutput(savedUserMock)).thenReturn(outputMock);

        var result = userService.createUser(input);

        assertSame(outputMock, result);
        verify(passwordCredentialUseCaseMock)
                .createPasswordCredential(TEST_USER_ID, TEST_PASSWORD, false);
    }

    @Test
    void testWhenUserInputNull_thenRejectCreation() {
        assertCreationRejected(null);
    }

    @Test
    void testWhenUserEmailBlank_thenRejectCreation() {
        assertCreationRejected(UserTestDataFactory.createInputWithEmail(" "));
    }

    @Test
    void testWhenUserPasswordBlank_thenRejectCreation() {
        assertCreationRejected(UserTestDataFactory.createInputWithPassword(" "));
    }

    @Test
    void testWhenUserMetadataNull_thenRejectCreation() {
        assertCreationRejected(UserTestDataFactory.createInputWithMetadata(null));
    }

    @Test
    void testWhenUserEmailAlreadyExists_thenRejectCreation() {
        var input = UserTestDataFactory.createValidInput();
        when(userRepositoryMock.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(input));

        verifyNoInteractions(userMapperMock, passwordCredentialUseCaseMock);
    }

    @Test
    void testWhenUserExists_thenReturnUser() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(userMapperMock.toOutput(userMock)).thenReturn(outputMock);

        var result = userService.getUser(TEST_USER_ID);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenUserDoesNotExist_thenRejectRequest() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(TEST_USER_ID));

        verifyNoInteractions(userMapperMock);
    }

    @Test
    void testWhenUsersExist_thenReturnUsers() {
        when(userRepositoryMock.findAll()).thenReturn(List.of(userMock));
        when(userMapperMock.toOutput(userMock)).thenReturn(outputMock);

        var result = userService.getUsers();

        assertEquals(List.of(outputMock), result);
    }

    @Test
    void testWhenUserUpdateValid_thenSaveUpdatedUser() {
        var input = UserTestDataFactory.createValidUpdateInput();
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(userMapperMock.toUpdatedUser(userMock, input)).thenReturn(mappedUserMock);
        when(userRepositoryMock.save(mappedUserMock)).thenReturn(savedUserMock);
        when(userMapperMock.toOutput(savedUserMock)).thenReturn(outputMock);

        var result = userService.updateUser(TEST_USER_ID, input);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenUserUpdateInputNull_thenRejectUpdate() {
        assertThrows(InvalidRequestException.class, () -> userService.updateUser(TEST_USER_ID, null));

        verifyNoInteractions(userRepositoryMock, userMapperMock);
    }

    @Test
    void testWhenUpdatedUserMetadataNull_thenRejectUpdate() {
        var input = UserTestDataFactory.createUpdateInputWithMetadata(null);

        assertThrows(InvalidRequestException.class, () -> userService.updateUser(TEST_USER_ID, input));

        verifyNoInteractions(userRepositoryMock, userMapperMock);
    }

    @Test
    void testWhenUserStatusValid_thenSaveChangedStatus() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));
        when(userMapperMock.withUpdatedStatus(userMock, UserStatus.SUSPENDED)).thenReturn(mappedUserMock);
        when(userRepositoryMock.save(mappedUserMock)).thenReturn(savedUserMock);
        when(userMapperMock.toOutput(savedUserMock)).thenReturn(outputMock);

        var result = userService.changeUserStatus(TEST_USER_ID, UserStatus.SUSPENDED);

        assertSame(outputMock, result);
    }

    @Test
    void testWhenUserStatusNull_thenRejectStatusChange() {
        assertThrows(InvalidRequestException.class, () -> userService.changeUserStatus(TEST_USER_ID, null));

        verifyNoInteractions(userRepositoryMock, userMapperMock);
    }

    @Test
    void testWhenUserExists_thenDeleteUserAndPasswordCredential() {
        when(userRepositoryMock.findById(TEST_USER_ID)).thenReturn(Optional.of(userMock));

        userService.deleteUser(TEST_USER_ID);

        verify(passwordCredentialUseCaseMock).deletePasswordCredential(TEST_USER_ID);
        verify(userRepositoryMock).deleteById(TEST_USER_ID);
    }

    private void assertCreationRejected(UserCreateInput input) {
        assertThrows(InvalidRequestException.class, () -> userService.createUser(input));
        verifyNoInteractions(userRepositoryMock, passwordCredentialUseCaseMock, userMapperMock);
    }
}
