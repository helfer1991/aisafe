package io.gh.helder.aisafe.identity.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.gh.helder.aisafe.identity.config.JwtProperties;
import io.gh.helder.aisafe.identity.domain.*;
import io.gh.helder.aisafe.identity.repository.RefreshTokenRepository;
import io.gh.helder.aisafe.identity.repository.UserRepository;
import io.gh.helder.aisafe.identity.web.dto.LoginRequest;
import io.gh.helder.aisafe.identity.web.dto.TokenPair;

@Service
@Transactional
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;
    private final JwtProperties props;

    AuthService(UserRepository users, RefreshTokenRepository refreshTokens,
                PasswordEncoder passwordEncoder, JwtService jwt, JwtProperties props) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.props = props;
    }

    public TokenPair login(LoginRequest request) {
        User user = users.findByEmail(new Email(request.email()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        if (!user.enabled()) {
            throw new AccountUnavailableException("Account is disabled");
        }
        if (!user.clearanceValid()) {
            throw new AccountUnavailableException("Security clearance expired on "
                    + user.clearanceExpiresOn());
        }
        if (!user.skillsAssessmentCurrent()) {
            throw new AccountUnavailableException("Skills assessment overdue; last assessed "
                    + user.skillsAssessedOn());
        }

        return issueFor(user);
    }

    public TokenPair refresh(String rawRefreshToken) {
        RefreshToken stored = refreshTokens.findByTokenHash(sha256(rawRefreshToken))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (!stored.usable()) {
            throw new InvalidCredentialsException("Refresh token expired or revoked");
        }

        User user = stored.user();
        if (!user.canAuthenticate()) {
            throw new AccountUnavailableException("Account is no longer able to authenticate");
        }

        stored.revoke();
        return issueFor(user);
    }

    public void logout(String rawRefreshToken) {
        refreshTokens.findByTokenHash(sha256(rawRefreshToken))
                .ifPresent(RefreshToken::revoke);
    }

    private TokenPair issueFor(User user) {
        String raw = randomToken();
        refreshTokens.save(new RefreshToken(
                user, sha256(raw), Instant.now().plus(props.refreshTokenTtl())));

        return new TokenPair(
                jwt.issueAccessToken(user),
                raw,
                props.accessTokenTtl().toSeconds());
    }

    private static String randomToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
