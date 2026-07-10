package io.github.janus.identity.adapter.in.web;

import io.github.janus.identity.adapter.in.web.dto.user.UserCreateRequest;
import io.github.janus.identity.adapter.in.web.dto.user.UserGetResponse;
import io.github.janus.identity.adapter.in.web.dto.user.UserUpdateRequest;
import io.github.janus.identity.adapter.in.web.dto.user.UserUpdateResponse;
import io.github.janus.identity.adapter.in.web.mapper.UserWebMapper;
import io.github.janus.identity.application.port.in.command.user.UserCreateCommand;
import io.github.janus.identity.application.port.in.command.user.UserDeleteCommand;
import io.github.janus.identity.application.port.in.query.user.UserGetAllQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByEmailQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByIdQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByUsernameQuery;
import io.github.janus.identity.application.port.in.usecase.user.CreateUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.DeleteUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.GetUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.UpdateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserWebMapper mapper;
    private final GetUserUseCase getUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(UserWebMapper mapper,
                          GetUserUseCase getUserUseCase,
                          CreateUserUseCase createUserUseCase,
                          UpdateUserUseCase updateUserUseCase, DeleteUserUseCase deleteUserUseCase) {
        this.mapper = mapper;
        this.getUserUseCase = getUserUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetResponse> getById(@PathVariable UUID id) {
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var query = new UserGetByIdQuery(id, tenantId);
        var domainUser = getUserUseCase.getbyId(query);
        return ResponseEntity.ok(mapper.toGetResponse(domainUser));
    }

    @GetMapping
    public ResponseEntity<UserGetResponse> getByEmail(@RequestParam String email) {
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var query = new UserGetByEmailQuery(email, tenantId);
        var domainUser = getUserUseCase.getbyEmail(query);
        return ResponseEntity.ok(mapper.toGetResponse(domainUser));
    }


    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserGetResponse> getByUsername(@PathVariable String username) {
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var query = new UserGetByUsernameQuery(username, tenantId);
        var domainUser = getUserUseCase.getbyUsername(query);
        return ResponseEntity.ok(mapper.toGetResponse(domainUser));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserGetResponse>> getAll(){
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var query = new UserGetAllQuery(tenantId);
        var domainUser = getUserUseCase.getAll(query);
        return ResponseEntity.ok(mapper.toGetAllResponse(domainUser));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody UserCreateRequest request) {
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var command = mapper.createReqToCommand(request, tenantId);
        createUserUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping
    public ResponseEntity<UserUpdateResponse> update(@RequestBody UserUpdateRequest request) {
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var command = mapper.updateReqToCommand(request, tenantId);
        var updated = updateUserUseCase.update(command);
        var updatedResponse = mapper.toUpdateResponse(updated);
        return ResponseEntity.status(HttpStatus.OK).body(updatedResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var command = new UserDeleteCommand(id, tenantId);
        deleteUserUseCase.delete(command);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
