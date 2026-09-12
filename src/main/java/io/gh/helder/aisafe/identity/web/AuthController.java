package io.gh.helder.aisafe.identity.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gh.helder.aisafe.identity.service.AuthService;
import io.gh.helder.aisafe.identity.web.dto.LoginRequest;
import io.gh.helder.aisafe.identity.web.dto.RefreshRequest;
import io.gh.helder.aisafe.identity.web.dto.TokenPair;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AuthService auth;

    AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    TokenPair login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }

    @PostMapping("/refresh")
    TokenPair refresh(@Valid @RequestBody RefreshRequest request) {
        return auth.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        auth.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}