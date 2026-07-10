package io.github.janus.common;

import jakarta.servlet.*;

import java.io.IOException;

//TODO: filter for reading Authorization header, jwt validation, decode claims, pulling out tenantId
//todo: store pulled tenantId in auth context (thread local ?)
//todo: inject "where tenantId =?" in every query manually or hibernate's @TenantId
public class JanusFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
