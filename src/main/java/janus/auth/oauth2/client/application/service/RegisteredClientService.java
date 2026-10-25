package janus.auth.oauth2.client.application.service;

import janus.auth.oauth2.client.application.dto.ClientRegistrationRequest;
import janus.auth.oauth2.client.application.dto.ClientRegistrationResponse;
import janus.auth.oauth2.client.application.exception.ClientRegistrationError;
import janus.auth.oauth2.client.application.exception.ClientRegistrationException;
import janus.auth.oauth2.client.application.mapper.RegisteredClientMapper;
import janus.auth.oauth2.client.application.port.out.RegisteredClientRepository;
import janus.auth.oauth2.client.application.port.usecase.RegisteredClientUseCase;
import janus.auth.oauth2.client.domain.type.ClientAuthenticationMethod;
import janus.auth.oauth2.client.domain.type.GrantType;
import janus.auth.oauth2.client.domain.type.ResponseType;
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

@Service
@RequiredArgsConstructor
public class RegisteredClientService implements RegisteredClientUseCase {

    private static final int CLIENT_SECRET_SIZE_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // RFC 6749 section 3.3:
    // scope       = scope-token *( SP scope-token )
    // scope-token = 1*( %x21 / %x23-5B / %x5D-7E )
    private static final Pattern OAUTH_SCOPE_PATTERN = Pattern.compile(
            "[\\x21\\x23-\\x5B\\x5D-\\x7E]+(?: [\\x21\\x23-\\x5B\\x5D-\\x7E]+)*"
    );

    private final RegisteredClientRepository registeredClientRepository;
    private final RegisteredClientMapper registeredClientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        validateRegistration(request);

        var clientId = UUID.randomUUID().toString();
        var clientSecret = generateClientSecret(request.tokenEndpointAuthMethod());
        var clientSecretHash = clientSecret == null ? null : passwordEncoder.encode(clientSecret);

        var client = registeredClientMapper.toEntity(
                request,
                clientId,
                clientSecretHash
        );
        var registeredClient = registeredClientRepository.save(client);

        return registeredClientMapper.toResponse(registeredClient, clientSecret);
    }

    private void validateRegistration(ClientRegistrationRequest request) {
        var grantTypes = request.grantTypes();
        var responseTypes = request.responseTypes();

        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            if (request.redirectUris().isEmpty()) {
                throw new ClientRegistrationException(
                        ClientRegistrationError.INVALID_REDIRECT_URI,
                        "Authorization code clients must register at least one redirect URI"
                );
            }
            if (!responseTypes.contains(ResponseType.CODE)) {
                throw new ClientRegistrationException(
                        ClientRegistrationError.INVALID_CLIENT_METADATA,
                        "The authorization_code grant requires the code response type"
                );
            }
        }

        if (responseTypes.contains(ResponseType.CODE)
                && !grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            throw new ClientRegistrationException(
                    ClientRegistrationError.INVALID_CLIENT_METADATA,
                    "The code response type requires the authorization_code grant"
            );
        }

        if (grantTypes.contains(GrantType.CLIENT_CREDENTIALS)
                && request.tokenEndpointAuthMethod() == ClientAuthenticationMethod.NONE) {
            throw new ClientRegistrationException(
                    ClientRegistrationError.INVALID_CLIENT_METADATA,
                    "The client_credentials grant requires a confidential client"
            );
        }

        request.redirectUris().forEach(this::validateRedirectUri);
        validateClientUri(request.clientUri());
        validateScope(request.scope());
    }

    private void validateRedirectUri(String value) {
        var uri = parseUri(
                value,
                ClientRegistrationError.INVALID_REDIRECT_URI,
                "Invalid redirect URI: " + value
        );

        if (!uri.isAbsolute() || uri.getFragment() != null) {
            throw new ClientRegistrationException(
                    ClientRegistrationError.INVALID_REDIRECT_URI,
                    "Redirect URI must be absolute and must not contain a fragment: " + value
            );
        }
    }

    private void validateClientUri(String value) {
        // client uri can be null since it is optional, but blank is invalid
        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new ClientRegistrationException(
                    ClientRegistrationError.INVALID_CLIENT_METADATA,
                    "Client URI must not be blank"
            );
        }
        var uri = parseUri(
                value,
                ClientRegistrationError.INVALID_CLIENT_METADATA,
                "Invalid client URI: " + value
        );

        var scheme = uri.getScheme();
        var isWebUri = scheme != null
                && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                && uri.getHost() != null;

        if (!isWebUri) {
            throw new ClientRegistrationException(
                    ClientRegistrationError.INVALID_CLIENT_METADATA,
                    "Client URI must be an HTTP or HTTPS URL with a valid host: " + value
            );
        }
    }

    private void validateScope(String scope) {
        if (scope == null) {
            return;
        }

        if (!OAUTH_SCOPE_PATTERN.matcher(scope).matches()) {
            throw new ClientRegistrationException(
                    ClientRegistrationError.INVALID_CLIENT_METADATA,
                    "Scope must contain valid values separated by single spaces"
            );
        }
    }

    private URI parseUri(String value, ClientRegistrationError error, String message) {
        try {
            return new URI(value);
        } catch (URISyntaxException | NullPointerException e) {
            throw new ClientRegistrationException(error, message);
        }
    }

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
