package io.github.janus.identity.adapter.in.web;

import io.github.janus.identity.adapter.in.web.dto.user.UserGetResponse;
import io.github.janus.identity.adapter.in.web.mapper.UserWebMapper;
import io.github.janus.identity.application.port.in.query.user.UserGetAllQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByEmailQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByIdQuery;
import io.github.janus.identity.application.port.in.query.user.UserGetByUsernameQuery;
import io.github.janus.identity.application.port.in.usecase.user.CreateUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.GetUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.UpdateUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    //no need for usergetrequest, map directly to Query

    private UserWebMapper mapper;
    private GetUserUseCase getUserUseCase;
    private CreateUserUseCase createUserUseCase;
    private UpdateUserUseCase updateUserUseCase;

    public UserController(UserWebMapper mapper,
                          GetUserUseCase getUserUseCase,
                          CreateUserUseCase createUserUseCase,
                          UpdateUserUseCase updateUserUseCase) {
        this.mapper = mapper;
        this.getUserUseCase = getUserUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
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


    @GetMapping
    public ResponseEntity<UserGetResponse> getByUsername(@RequestParam String username) {
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var query = new UserGetByUsernameQuery(username, tenantId);
        var domainUser = getUserUseCase.getbyUsername(query);
        return ResponseEntity.ok(mapper.toGetResponse(domainUser));
    }

    public ResponseEntity<List<UserGetResponse>> getAll(){
        var tenantId = UUID.randomUUID(); // todo: fetch tenant id from jwt in auth header
        var query = new UserGetAllQuery(tenantId);
        var domainUser = getUserUseCase.getAll(query);
        return ResponseEntity.ok(mapper.toGetAllResponse(domainUser));
    }

}
