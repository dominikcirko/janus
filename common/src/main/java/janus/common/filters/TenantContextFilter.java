package janus.common.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import janus.common.TenantContext;
import janus.common.exceptionhandler.exception.InvalidConfigurationException;
import janus.common.security.token.VerifiedAccessToken;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
public final class TenantContextFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Object attribute = request.getAttribute(AccessTokenAuthenticationFilter.VERIFIED_ACCESS_TOKEN_ATTRIBUTE);
        if (!(attribute instanceof VerifiedAccessToken verifiedToken) || verifiedToken.tenantId() == null) {
            var ex = new InvalidConfigurationException(
                    "TenantContextFilter requires a verified tenant from the authentication filter"
            );
            if (exceptionResolver.resolveException(request, response, null, ex) == null) {
                throw ex;
            }
            return;
        }

        try {
            TenantContext.callWithTenant(verifiedToken.tenantId(), () -> {
                filterChain.doFilter(request, response);
                return null;
            });
        } catch (IOException | ServletException | RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServletException("Tenant-bound request processing failed", ex);
        }
    }
}
