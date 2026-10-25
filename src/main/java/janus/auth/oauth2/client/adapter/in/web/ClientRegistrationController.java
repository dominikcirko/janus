package janus.auth.oauth2.client.adapter.in.web;

import jakarta.validation.Valid;
import janus.auth.oauth2.client.application.dto.ClientRegistrationRequest;
import janus.auth.oauth2.client.application.dto.ClientRegistrationResponse;
import janus.auth.oauth2.client.application.port.usecase.RegisteredClientUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{version}/clients")
@RequiredArgsConstructor
public class ClientRegistrationController {

    private final RegisteredClientUseCase registeredClientUseCase;

    @PostMapping(
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ClientRegistrationResponse> registerClient(
            @Valid @RequestBody ClientRegistrationRequest request) {
        var response = registeredClientUseCase.registerClient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}
