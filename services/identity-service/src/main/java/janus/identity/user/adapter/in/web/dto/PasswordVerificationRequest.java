package janus.identity.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordVerificationRequest(@NotBlank String password) {
}
