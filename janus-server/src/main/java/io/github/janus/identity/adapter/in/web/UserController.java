package io.github.janus.identity.adapter.in.web;

import io.github.janus.identity.adapter.in.web.dto.UserGetRequest;
import io.github.janus.identity.application.port.in.usecase.user.CreateUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.GetUserUseCase;
import io.github.janus.identity.application.port.in.usecase.user.UpdateUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    //no need for usergetrequest, map directly to Query

    private GetUserUseCase getUserUseCase;
    private CreateUserUseCase createUserUseCase;
    private UpdateUserUseCase updateUserUseCase;

    @GetMapping
    public ResponseEntity<UserGetRequest> getById(@RequestParam UUID id) {

    }

}
