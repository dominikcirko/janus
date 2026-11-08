package janus.auth.oauth2.authorization.application.port.usecase;

import janus.auth.oauth2.authorization.application.port.usecase.model.AuthorizationInput;

public interface AuthorizationUseCase {

    String authorize(AuthorizationInput input);
}
