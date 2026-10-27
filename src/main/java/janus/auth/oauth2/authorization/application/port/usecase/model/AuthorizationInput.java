package janus.auth.oauth2.authorization.application.port.usecase.model;

/**
 * Application representation of an OAuth authorization request.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">RFC 6749, Section 4.1.1</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7636#section-4.3">RFC 7636, Section 4.3</a>
 */
public record AuthorizationInput(
        String responseType,
        String clientId,
        String redirectUri,
        String scope,
        String state,
        String codeChallenge,
        String codeChallengeMethod) {}
