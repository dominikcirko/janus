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

    /**
     * Enforces the space-delimited OAuth scope-token syntax.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">RFC 6749, Section 3.3</a>
     */
    private static final Pattern OAUTH_SCOPE_PATTERN = Pattern.compile(
            "[\\x21\\x23-\\x5B\\x5D-\\x7E]+(?: [\\x21\\x23-\\x5B\\x5D-\\x7E]+)*"
    );

    /**
     * Enforces the 43-character Base64URL representation produced by S256.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7636#section-4.2">RFC 7636, Section 4.2</a>
     */
    private static final Pattern S256_CODE_CHALLENGE_PATTERN = Pattern.compile("[A-Za-z0-9_-]{43}");

    /**
     * Validates an authorization-code request before authenticating the resource owner.
     * Client status is a Janus lifecycle policy; OAuth defines its rejection as
     * {@code unauthorized_client}.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">RFC 6749, Section 4.1.1</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1">RFC 6749, Section 4.1.2.1</a>
     */
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

    /**
     * Requires a redirect URI under Janus policy and matches it exactly against a
     * registered URI. Invalid client or redirect data must not be used for a redirect.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.1.2.3">RFC 6749, Section 3.1.2.3</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1">RFC 6749, Section 4.1.2.1</a>
     */
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

    /**
     * Requires {@code response_type=code} and verifies that the client may use it.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">RFC 6749, Section 4.1.1</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1">RFC 6749, Section 4.1.2.1</a>
     */
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

    /**
     * Validates scope syntax and limits the request to scopes registered for the client.
     * Because Janus has no default-scope policy, an omitted scope is rejected.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">RFC 6749, Section 3.3</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1">RFC 6749, Section 4.1.2.1</a>
     */
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

    /**
     * Requires a valid PKCE challenge and the S256 transformation method. Janus does
     * not accept {@code plain} because it exposes the verifier in the authorization request.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7636#section-4.2">RFC 7636, Section 4.2</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7636#section-4.3">RFC 7636, Section 4.3</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7636#section-4.4.1">RFC 7636, Section 4.4.1</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9700#section-2.1.1">RFC 9700, Section 2.1.1</a>
     */
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
