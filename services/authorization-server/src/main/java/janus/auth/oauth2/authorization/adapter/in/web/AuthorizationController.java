package janus.auth.oauth2.authorization.adapter.in.web;

import janus.auth.oauth2.authorization.application.port.usecase.AuthorizationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(name = "/oauth2/authorize")
public class AuthorizationController {

    private final AuthorizationUseCase authorizationUseCase;

//    @GetMapping
//    public ResponseEntity<AuthorizationResponse> authorize(@Valid @RequestBody AuthorizationRequest request){
//
//    }


}
