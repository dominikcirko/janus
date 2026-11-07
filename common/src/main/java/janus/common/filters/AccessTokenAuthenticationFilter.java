package janus.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import janus.common.security.token.AccessTokenVerifier;
import janus.common.security.token.VerifiedAccessToken;
import janus.common.security.token.exception.InvalidAccessTokenException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
public final class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String VERIFIED_ACCESS_TOKEN_ATTRIBUTE =
            AccessTokenAuthenticationFilter.class.getName() + ".verifiedAccessToken";

    private static final String BEARER_PREFIX = "Bearer ";

    private final AccessTokenVerifier accessTokenVerifier;
    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        VerifiedAccessToken verifiedToken;
        try {
            verifiedToken = accessTokenVerifier.verify(readBearerToken(request));
        } catch (RuntimeException ex) {
            if (ex instanceof InvalidAccessTokenException) {
                response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
            }
            // This filter runs before controllers, outside MVC's automatic exception handling.
            // Explicitly ask Spring's resolver to invoke our exception handler and write the error response.
            if (exceptionResolver.resolveException(request, response, null, ex) == null) {
                throw ex;
            }
            return;
        }

        request.setAttribute(VERIFIED_ACCESS_TOKEN_ATTRIBUTE, verifiedToken);
        try {
            filterChain.doFilter(request, response);
        } finally {
            request.removeAttribute(VERIFIED_ACCESS_TOKEN_ATTRIBUTE);
        }
    }

    private String readBearerToken(HttpServletRequest request) {
        var authorizations = request.getHeaders(HttpHeaders.AUTHORIZATION);
        if (!authorizations.hasMoreElements()) {
            throw new InvalidAccessTokenException("Bearer access token is required");
        }

        String authorization = authorizations.nextElement();
        if (authorizations.hasMoreElements()) {
            throw new InvalidAccessTokenException("Only one Authorization header is allowed");
        }
        if (!authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            throw new InvalidAccessTokenException("Authorization must use the Bearer scheme");
        }

        String token = authorization.substring(BEARER_PREFIX.length()).strip();
        if (token.isEmpty()) {
            throw new InvalidAccessTokenException("Bearer access token must not be empty");
        }
        return token;
    }
}
