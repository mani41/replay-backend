package com.payment.personal.auth.service;

import com.google.firebase.auth.FirebaseToken;


import com.payment.personal.auth.dto.User;
import com.payment.personal.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User createOrUpdate(FirebaseToken token) {

        User user = repository
                .findByFirebaseUid(token.getUid())
                .orElse(User.builder().build());

        user.setFirebaseUid(token.getUid());
        user.setEmail(token.getEmail());
        user.setName(token.getName());
        user.setPictureUrl(token.getPicture());
        user.setLastLogin(Instant.now());

        return repository.save(user);
    }

}
