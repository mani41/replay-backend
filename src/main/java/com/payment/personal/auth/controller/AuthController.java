package com.payment.personal.auth.controller;

import com.google.firebase.auth.FirebaseToken;
import com.payment.personal.auth.dto.AuthRequest;
import com.payment.personal.auth.dto.AuthResponse;
import com.payment.personal.auth.dto.User;
import com.payment.personal.auth.service.FirebaseAuthService;
import com.payment.personal.auth.service.JwtService;
import com.payment.personal.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final FirebaseAuthService firebaseAuthService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/google")
    public AuthResponse authenticate(
            @RequestBody AuthRequest request) throws Exception {

        FirebaseToken firebaseToken =
                firebaseAuthService.verifyToken(request.idToken());

        User user = userService.createOrUpdate(firebaseToken);

        String jwt = jwtService.generateToken(user);

        return new AuthResponse(jwt);
    }
}