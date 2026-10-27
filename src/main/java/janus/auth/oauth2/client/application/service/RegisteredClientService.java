package janus.auth.oauth2.client.application.service;

import janus.auth.oauth2.client.application.mapper.RegisteredClientMapper;
import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.client.application.port.usecase.RegisteredClientUseCase;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientInput;
import janus.auth.oauth2.client.application.port.usecase.model.RegisteredClientOutput;
import janus.auth.oauth2.client.domain.exception.RegisteredClientError;
import janus.auth.oauth2.client.domain.exception.RegisteredClientException;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.common.type.GrantType;
import janus.auth.oauth2.common.type.ResponseType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validates and persists dynamic client registrations.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.1">RFC 7591, Section 3.1</a>
 */
@Service
@RequiredArgsConstructor
public class RegisteredClientService implements RegisteredClientUseCase {

    private static final int CLIENT_SECRET_SIZE_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RegisteredClientRepository registeredClientRepository;
    private final RegisteredClientMapper registeredClientMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Enforces the space-delimited scope-token syntax.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">RFC 6749, Section 3.3</a>
     */
    private static final Pattern OAUTH_SCOPE_PATTERN = Pattern.compile(
            "[\\x21\\x23-\\x5B\\x5D-\\x7E]+(?: [\\x21\\x23-\\x5B\\x5D-\\x7E]+)*"
    );

    /**
     * Registers the client and returns the metadata required in a successful response.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.1">RFC 7591, Section 3.2.1</a>
     */
    @Override
    @Transactional
    public RegisteredClientOutput registerClient(RegisteredClientInput input) {
        validateRegistration(input);

        var clientId = UUID.randomUUID().toString();
        var clientSecret = generateClientSecret(input.tokenEndpointAuthMethod());
        String clientSecretHash = null;
        if (clientSecret != null) {
            clientSecretHash = passwordEncoder.encode(clientSecret);
        }

        var client = registeredClientMapper.toEntity(
                input,
                clientId,
                clientSecretHash
        );
        var registeredClient = registeredClientRepository.save(client);

        return registeredClientMapper.toOutput(registeredClient, clientSecret);
    }

    /**
     * Rejects inconsistent grant/response types, missing redirect URIs, and public
     * clients attempting to use the client credentials grant.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2.1">RFC 7591, Section 2.1</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-5">RFC 7591, Section 5</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.4">RFC 6749, Section 4.4</a>
     */
    private void validateRegistration(RegisteredClientInput input) {
        var grantTypes = input.grantTypes();
        var responseTypes = input.responseTypes();

        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            if (input.redirectUris().isEmpty()) {
                throw new RegisteredClientException(
                        RegisteredClientError.INVALID_REDIRECT_URI,
                        "Authorization code clients must register at least one redirect URI"
                );
            }
            if (!responseTypes.contains(ResponseType.CODE)) {
                throw new RegisteredClientException(
                        RegisteredClientError.INVALID_CLIENT_METADATA,
                        "The authorization_code grant requires the code response type"
                );
            }
        }

        if (responseTypes.contains(ResponseType.CODE)
                && !grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            throw new RegisteredClientException(
                    RegisteredClientError.INVALID_CLIENT_METADATA,
                    "The code response type requires the authorization_code grant"
            );
        }

        if (grantTypes.contains(GrantType.CLIENT_CREDENTIALS)
                && input.tokenEndpointAuthMethod() == ClientAuthenticationMethod.NONE) {
            throw new RegisteredClientException(
                    RegisteredClientError.INVALID_CLIENT_METADATA,
                    "The client_credentials grant requires a confidential client"
            );
        }

        input.redirectUris().forEach(this::validateRedirectUri);
        validateClientUri(input.clientUri());
        validateScope(input.scope());
    }

    /**
     * Requires an absolute redirect URI without a fragment.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.1.2">RFC 6749, Section 3.1.2</a>
     */
    private void validateRedirectUri(String value) {
        var uri = parseUri(
                value,
                RegisteredClientError.INVALID_REDIRECT_URI,
                "Invalid redirect URI: " + value
        );

        if (!uri.isAbsolute() || uri.getFragment() != null) {
            throw new RegisteredClientException(
                    RegisteredClientError.INVALID_REDIRECT_URI,
                    "Redirect URI must be absolute and must not contain a fragment: " + value
            );
        }
    }

    /**
     * Applies Janus's syntactic check for the RFC valid-web-page requirement.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-2">RFC 7591, Section 2</a>
     */
    private void validateClientUri(String value) {
        // client uri can be null since it is optional, but blank is invalid
        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new RegisteredClientException(
                    RegisteredClientError.INVALID_CLIENT_METADATA,
                    "Client URI must not be blank"
            );
        }
        var uri = parseUri(
                value,
                RegisteredClientError.INVALID_CLIENT_METADATA,
                "Invalid client URI: " + value
        );

        var scheme = uri.getScheme();
        var isWebUri = scheme != null
                && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                && uri.getHost() != null;

        if (!isWebUri) {
            throw new RegisteredClientException(
                    RegisteredClientError.INVALID_CLIENT_METADATA,
                    "Client URI must be an HTTP or HTTPS URL with a valid host: " + value
            );
        }
    }

    /**
     * Validates the RFC scope-token syntax when scope metadata is present.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">RFC 6749, Section 3.3</a>
     */
    private void validateScope(String scope) {
        if (scope == null) {
            return;
        }

        if (!OAUTH_SCOPE_PATTERN.matcher(scope).matches()) {
            throw new RegisteredClientException(
                    RegisteredClientError.INVALID_CLIENT_METADATA,
                    "Scope must contain valid values separated by single spaces"
            );
        }
    }

    private URI parseUri(String value, RegisteredClientError error, String message) {
        try {
            return new URI(value);
        } catch (URISyntaxException | NullPointerException e) {
            throw new RegisteredClientException(error, message);
        }
    }

    /**
     * Generates the optional secret returned for clients that use authentication.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.1">RFC 7591, Section 3.2.1</a>
     */
    private String generateClientSecret(ClientAuthenticationMethod authenticationMethod) {
        if (authenticationMethod == ClientAuthenticationMethod.NONE) {
            return null;
        }

        var secretBytes = new byte[CLIENT_SECRET_SIZE_BYTES];
        SECURE_RANDOM.nextBytes(secretBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(secretBytes);
    }
}
