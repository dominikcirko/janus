package janus.auth.orchestration.adapter.in.web;


import janus.auth.orchestration.adapter.in.web.dto.LoginRequest;
import janus.auth.orchestration.adapter.in.web.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return null;
    }

}