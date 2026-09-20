package janus.auth.orchestration.adapter.in.web.dto;

public record LoginResponse(String accessToken,
                            String refreshToken) {}
