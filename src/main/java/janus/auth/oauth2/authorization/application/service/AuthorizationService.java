package janus.auth.oauth2.authorization.application.service;

import janus.auth.oauth2.authorization.application.port.usecase.AuthorizationUseCase;
import janus.auth.oauth2.authorization.application.port.usecase.model.AuthorizationInput;
import janus.auth.oauth2.authorization.domain.exception.AuthorizationError;
import janus.auth.oauth2.authorization.domain.exception.AuthorizationException;
import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.common.type.ResponseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements AuthorizationUseCase {

    private final RegisteredClientRepository clientRepository;

    public String authorize(AuthorizationInput input) throws URISyntaxException {

        if(input.clientId() == null || input.clientId().isBlank()){
            throw new AuthorizationException(AuthorizationError.INVALID_REQUEST, "client_id must not be null");
        }

        var client = clientRepository.findByClientId(input.clientId()).orElseThrow(
                () -> new AuthorizationException(AuthorizationError.INVALID_REQUEST, "Invalid client_id")
        );

        if(input.redirectUri() == null || input.redirectUri().isBlank()){
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST, "redirect_uri must not be null or blank"
            );
        }
        if(!client.getRedirectUris().contains(input.redirectUri())){
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST, "Provided redirect_uri is not registered"
            );
        }

        if(!Objects.equals(input.responseType(), ResponseType.CODE.value())){
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST,
                    "Response type must be 'code'",
                    new URI(input.redirectUri()),
                    input.state()
            );
        }

    }

}
