package janus.identity.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import janus.identity.user.domain.type.UserStatus;

public record UserStatusRequest(@NotNull UserStatus status) {
}
