package janus.auth.oauth2.client.application.port.usecase;

import janus.auth.oauth2.client.application.dto.ClientRegistrationRequest;
import janus.auth.oauth2.client.application.dto.ClientRegistrationResponse;

public interface RegisteredClientUseCase {
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request);
}
