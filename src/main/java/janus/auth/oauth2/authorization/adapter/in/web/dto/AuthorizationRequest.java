package janus.auth.oauth2.authorization.adapter.in.web.dto;

import janus.auth.oauth2.common.type.ResponseType;

public record AuthorizationRequest(
        ResponseType responseType,
        String clientId,
        String redirectUri,
        String scope,
        String state,
        String codeChallenge,
        String codeChallengeMethod) {}