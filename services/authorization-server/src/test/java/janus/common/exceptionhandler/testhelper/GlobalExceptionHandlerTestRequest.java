package janus.common.exceptionhandler.testhelper;

import jakarta.validation.constraints.NotBlank;

public record GlobalExceptionHandlerTestRequest(@NotBlank String name) {
}
