package janus.auth.oauth2.client.adapter.in.web;

import jakarta.validation.Valid;
import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientRequest;
import janus.auth.oauth2.client.adapter.in.web.dto.RegisteredClientResponse;
import janus.auth.oauth2.client.adapter.in.web.mapper.RegisteredClientWebMapper;
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

/**
 * Exposes the OAuth dynamic client registration endpoint, which must be deployed over TLS.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3">RFC 7591, Section 3</a>
 */
@RestController
@RequestMapping("/api/{version}/clients")
@RequiredArgsConstructor
public class RegisteredClientController {

    private final RegisteredClientUseCase registeredClientUseCase;
    private final RegisteredClientWebMapper registeredClientWebMapper;

    /**
     * Accepts registration metadata and returns the registered client with HTTP 201.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.1">RFC 7591, Section 3.1</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.1">RFC 7591, Section 3.2.1</a>
     */
    @PostMapping(
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RegisteredClientResponse> registerClient(
        @Valid @RequestBody RegisteredClientRequest request) {
        var input = registeredClientWebMapper.toInput(request);
        var output = registeredClientUseCase.registerClient(input);
        var response = registeredClientWebMapper.toResponse(output);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}
