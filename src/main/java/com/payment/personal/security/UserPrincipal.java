package com.payment.personal.security;

import lombok.Getter;

import java.security.Principal;
import java.util.UUID;

public class UserPrincipal implements Principal {

    @Getter
    private final UUID userId;
    private final String email;

    public UserPrincipal(UUID userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String getName() {
        return email;
    }
}
