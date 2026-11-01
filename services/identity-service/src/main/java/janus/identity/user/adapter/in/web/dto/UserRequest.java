package janus.identity.user.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UserRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        String displayName,
        String givenName,
        String familyName,
        String pictureUri,
        @NotNull Map<String, Object> userMetadata
) {
}
