package com.learning.serviceb.filters;

import com.learning.serviceb.security.AuthContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthContext.setIsAuthenticated(true);
        AuthContext.setRequestId(UUID.randomUUID().toString().toUpperCase().replace("-", ""));
        log.info("Request on Service B -requestId {}",AuthContext.getRequestId());
        filterChain.doFilter(request, response);
    }
}
