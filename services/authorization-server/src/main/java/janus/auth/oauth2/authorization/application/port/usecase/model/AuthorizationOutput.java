package janus.auth.oauth2.authorization.application.port.usecase.model;

import java.net.URI;

/**
 * Application result used to redirect a successful authorization response.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2">RFC 6749, Section 4.1.2</a>
 */
public record AuthorizationOutput(URI validatedRedirectUri, String code, String state) {}
