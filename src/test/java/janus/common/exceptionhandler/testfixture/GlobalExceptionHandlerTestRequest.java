package janus.common.exceptionhandler.testfixture;

import jakarta.validation.constraints.NotBlank;

public record GlobalExceptionHandlerTestRequest(@NotBlank String name) {}
