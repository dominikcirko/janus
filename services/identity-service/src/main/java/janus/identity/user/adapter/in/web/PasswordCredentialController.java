package janus.identity.user.adapter.in.web;

import jakarta.validation.Valid;
import janus.identity.user.adapter.in.web.dto.PasswordChangeRequest;
import janus.identity.user.adapter.in.web.dto.PasswordVerificationRequest;
import janus.identity.user.adapter.in.web.dto.PasswordVerificationResponse;
import janus.identity.user.application.port.usecase.PasswordCredentialUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/users/{userId}/password")
@RequiredArgsConstructor
public class PasswordCredentialController {

    private final PasswordCredentialUseCase passwordCredentialUseCase;

    @PostMapping(
            value = "/verify",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PasswordVerificationResponse verifyPassword(
            @PathVariable UUID userId,
            @Valid @RequestBody PasswordVerificationRequest request) {
        var valid = passwordCredentialUseCase.verifyPassword(userId, request.password());

        return new PasswordVerificationResponse(valid);
    }

    @PutMapping(version = "1+", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        passwordCredentialUseCase.changePassword(userId, request.currentPassword(), request.newPassword());

        return ResponseEntity.noContent().build();
    }
}
