package janus.auth.oauth2.client.application.port.usecase;

import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;

public interface RegisteredClientUseCase {
    RegisteredClientOutput registerClient(RegisteredClientInput input);
}
