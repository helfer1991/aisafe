package io.gh.helder.aisafe.identity.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.gh.helder.aisafe.identity.config.JwtProperties;
import io.gh.helder.aisafe.identity.domain.Role;
import io.gh.helder.aisafe.identity.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final JwtProperties props;
    private final SecretKey key;

    JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String issueAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(props.issuer())
                .subject(user.id().toString())
                .claim("email", user.email().value())
                .claim("name", user.name())
                .claim("roles", user.roles().stream().map(Role::name).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.accessTokenTtl())))
                .signWith(key)
                .compact();
    }

    public Optional<Claims> parse(String token) {
        try {
            return Optional.of(Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(props.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();  
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> rolesFrom(Claims claims) {
        return claims.get("roles", List.class);
    }
}