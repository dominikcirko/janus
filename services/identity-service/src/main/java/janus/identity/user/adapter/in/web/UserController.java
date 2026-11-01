package janus.identity.user.adapter.in.web;

import jakarta.validation.Valid;
import janus.identity.user.adapter.in.web.dto.UserRequest;
import janus.identity.user.adapter.in.web.dto.UserResponse;
import janus.identity.user.adapter.in.web.dto.UserStatusRequest;
import janus.identity.user.adapter.in.web.dto.UserUpdateRequest;
import janus.identity.user.adapter.in.web.mapper.UserWebMapper;
import janus.identity.user.application.port.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final UserWebMapper userWebMapper;

    @PostMapping(
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        var input = userWebMapper.toCreateInput(request);
        var output = userUseCase.createUser(input);
        var response = userWebMapper.toResponse(output);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(value = "/{userId}", version = "1+", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse getUser(@PathVariable UUID userId) {
        var output = userUseCase.getUser(userId);

        return userWebMapper.toResponse(output);
    }

    @GetMapping(version = "1+", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserResponse> getUsers() {
        var output = userUseCase.getUsers();

        return userWebMapper.toResponse(output);
    }

    @PutMapping(
            value = "/{userId}",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        var input = userWebMapper.toUpdateInput(request);
        var output = userUseCase.updateUser(userId, input);

        return userWebMapper.toResponse(output);
    }

    @PatchMapping(
            value = "/{userId}/status",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse changeUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusRequest request) {
        var output = userUseCase.changeUserStatus(userId, request.status());

        return userWebMapper.toResponse(output);
    }

    @DeleteMapping(value = "/{userId}", version = "1+")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userUseCase.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
