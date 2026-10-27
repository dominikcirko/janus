package janus.auth.oauth2.authorization.adapter.in.web.dto;

public record AuthorizationRequest(
        String responseType,
        String clientId,
        String redirectUri,
        String scope,
        String state,
        String codeChallenge,
        String codeChallengeMethod) {}