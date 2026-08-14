package com.payment.personal.security;

import com.payment.personal.auth.dto.User;
import com.payment.personal.auth.repository.UserRepository;
import com.payment.personal.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JWT FILTER: {} {}", request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        log.info("JWT FILTER Authorization present: {}", authHeader != null);

        // No Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // Invalid token
        if (!jwtService.isValid(jwt)) {
            log.info("JWT FILTER: TOKEN INVALID");
            filterChain.doFilter(request, response);
            return;
        }

        UUID userId = jwtService.extractUserId(jwt);

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            log.info("JWT FILTER: USER NOT FOUND: {}", userId);
            filterChain.doFilter(request, response);
            return;
        }

        User user = optionalUser.get();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("JWT FILTER: authentication SUCCESS");

        filterChain.doFilter(request, response);
    }
}
