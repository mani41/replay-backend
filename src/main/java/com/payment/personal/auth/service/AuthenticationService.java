package com.payment.personal.auth.service;

import com.payment.personal.auth.dto.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class AuthenticationService {

    public User getCurrentUser() throws AccessDeniedException {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof User user)) {

            throw new AccessDeniedException("User is not authenticated");
        }

        return user;
    }
}
