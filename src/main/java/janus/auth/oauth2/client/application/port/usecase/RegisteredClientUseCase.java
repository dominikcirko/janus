package janus.auth.oauth2.client.application.port.usecase;

import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;

/**
 * Application port for the dynamic client registration operation.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.1">RFC 7591, Section 3.1</a>
 */
public interface RegisteredClientUseCase {
    RegisteredClientOutput registerClient(RegisteredClientInput input);
}
