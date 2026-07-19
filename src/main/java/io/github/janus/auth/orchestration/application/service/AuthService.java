package io.github.janus.auth.orchestration.application.service;

import io.github.janus.auth.orchestration.adapter.in.web.dto.LoginRequest;
import io.github.janus.auth.orchestration.adapter.in.web.dto.LoginResponse;
import io.github.janus.identity.user.application.port.in.usecase.GetUserUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    GetUserUseCase getUserUseCase;

    LoginResponse login(LoginRequest loginRequest) {

    }

}
