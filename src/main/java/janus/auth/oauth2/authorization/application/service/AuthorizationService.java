package janus.auth.oauth2.authorization.application.service;

import janus.auth.oauth2.authorization.application.port.usecase.AuthorizationUseCase;
import janus.auth.oauth2.authorization.application.port.usecase.model.AuthorizationInput;
import janus.auth.oauth2.authorization.domain.exception.AuthorizationError;
import janus.auth.oauth2.authorization.domain.exception.AuthorizationException;
import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClient;
import janus.auth.oauth2.common.type.CodeChallengeMethod;
import janus.auth.oauth2.common.type.ResponseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements AuthorizationUseCase {

    private final RegisteredClientRepository clientRepository;

    private static final Pattern OAUTH_SCOPE_PATTERN = Pattern.compile(
            "[\\x21\\x23-\\x5B\\x5D-\\x7E]+(?: [\\x21\\x23-\\x5B\\x5D-\\x7E]+)*"
    );

    private static final Pattern S256_CODE_CHALLENGE_PATTERN =
            Pattern.compile("[A-Za-z0-9_-]{43}");

    public String authorize(AuthorizationInput input) {

        if(input.clientId() == null || input.clientId().isBlank()){
            throw new AuthorizationException(AuthorizationError.INVALID_REQUEST, "client_id must not be null or blank");
        }

        RegisteredClient client = clientRepository.findByClientId(input.clientId()).orElseThrow(
                () -> new AuthorizationException(AuthorizationError.INVALID_REQUEST, "Invalid client_id")
        );

        URI validatedRedirectUri = validateRedirectUri(input, client);

        if (!client.isActive()) {
            throw new AuthorizationException(
                    AuthorizationError.UNAUTHORIZED_CLIENT,
                    "Client is not active",
                    validatedRedirectUri,
                    input.state()
            );
        }

        validateResponseType(input, client, validatedRedirectUri);
        validateCodeChallenge(input, validatedRedirectUri);
        Set<String> scopes = parseAndValidateScopes(input, client, validatedRedirectUri);



        return "";
    }

    private URI validateRedirectUri(AuthorizationInput input, RegisteredClient client) {
        if(input.redirectUri() == null || input.redirectUri().isBlank()){
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST, "redirect_uri must not be null or blank"
            );
        }
        if(!client.hasRedirectUri(input.redirectUri())){
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST, "Provided redirect_uri is not registered"
            );
        }
        return URI.create(input.redirectUri());
    }

    private void validateResponseType(AuthorizationInput input, RegisteredClient client,
                                      URI validatedRedirectUri) {
        if (input.responseType() == null || input.responseType().isBlank()) {
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST,
                    "response_type is required",
                    validatedRedirectUri,
                    input.state()
            );
        }

        if(!Objects.equals(input.responseType(), ResponseType.CODE.value())){
            throw new AuthorizationException(
                    AuthorizationError.UNSUPPORTED_RESPONSE_TYPE,
                    "Response type must be 'code'",
                    validatedRedirectUri,
                    input.state()
            );
        }

        if (!client.supportsResponseType(ResponseType.CODE)) {
            throw new AuthorizationException(
                    AuthorizationError.UNAUTHORIZED_CLIENT,
                    "Client is not authorized to use the 'code' response type",
                    validatedRedirectUri,
                    input.state()
            );
        }
    }

    private Set<String> parseAndValidateScopes(AuthorizationInput input, RegisteredClient client,
                                               URI validatedRedirectUri) {
        if(input.scope() == null ||input.scope().isBlank()){
            throw new AuthorizationException(
                    AuthorizationError.INVALID_SCOPE,
                    "Requested scope must not be blank",
                    validatedRedirectUri, input.state()
            );
        }

        if (!OAUTH_SCOPE_PATTERN.matcher(input.scope()).matches()) {
            throw new AuthorizationException(
                    AuthorizationError.INVALID_SCOPE,
                    "Scope must contain valid values separated by single spaces",
                    validatedRedirectUri,
                    input.state()
            );
        }

        var requestedScopes = Arrays.stream(input.scope().split(" "))
                .collect(Collectors.toSet());

        if(!client.getScopes().containsAll(requestedScopes)){
            throw new AuthorizationException(
                    AuthorizationError.INVALID_SCOPE,
                    "One or more requested scopes are invalid",
                    validatedRedirectUri, input.state()
            );
        }


        return requestedScopes;
    }

    private void validateCodeChallenge(AuthorizationInput input, URI validatedRedirectUri){
        if (input.codeChallenge() == null
                || input.codeChallenge().isBlank()
                || !S256_CODE_CHALLENGE_PATTERN.matcher(input.codeChallenge()).matches()) {
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST,
                    "code_challenge must be a valid S256 challenge",
                    validatedRedirectUri,
                    input.state()
            );
        }

        if (!Objects.equals(input.codeChallengeMethod(), CodeChallengeMethod.S256.value())) {
            throw new AuthorizationException(
                    AuthorizationError.INVALID_REQUEST,
                    "code_challenge_method must be 'S256'",
                    validatedRedirectUri,
                    input.state()
            );
        }
    }

}
