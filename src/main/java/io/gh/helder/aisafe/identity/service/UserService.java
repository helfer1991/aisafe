package io.gh.helder.aisafe.identity.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.gh.helder.aisafe.identity.config.EmailProperties;
import io.gh.helder.aisafe.identity.domain.DuplicateEmailException;
import io.gh.helder.aisafe.identity.domain.Email;
import io.gh.helder.aisafe.identity.domain.EmailDomainNotAllowedException;
import io.gh.helder.aisafe.identity.domain.User;
import io.gh.helder.aisafe.identity.domain.UserNotFoundException;
import io.gh.helder.aisafe.identity.repository.RefreshTokenRepository;
import io.gh.helder.aisafe.identity.repository.UserRepository;
import io.gh.helder.aisafe.identity.web.dto.CreateUserRequest;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final EmailProperties emailProperties;

    UserService(UserRepository users, RefreshTokenRepository refreshTokens,
                PasswordEncoder passwordEncoder,
                EmailProperties emailProperties) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.emailProperties = emailProperties;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE_OPERATOR')")
    public User register(CreateUserRequest request) {
        var email = new Email(request.email());

        if (!emailProperties.allowedDomains().contains(email.domain())) {
            throw new EmailDomainNotAllowedException(
                    "Email domain not permitted: " + email.domain());
        }
        if (users.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already registered: " + email);
        }

        return users.save(new User(
                email,
                passwordEncoder.encode(request.password()),
                request.name(),
                request.phoneNumber(),
                request.roles(),
                request.clearanceExpiresOn(),
                request.skillsAssessedOn()));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User disable(UUID id) {
        User user = byId(id);
        user.disable();
        refreshTokens.revokeAllForUser(user);   // kill live sessions immediately
        return user;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User enable(UUID id) {
        User user = byId(id);
        user.enable();
        return user;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE_OPERATOR')")
    public User renewClearance(UUID id, LocalDate expiresOn) {
        User user = byId(id);
        user.renewClearance(expiresOn);
        return user;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'BACKOFFICE_OPERATOR')")
    public User recordSkillsAssessment(UUID id, LocalDate assessedOn) {
        User user = byId(id);
        user.recordSkillsAssessment(assessedOn);
        return user;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> findAll(Pageable pageable) {
        return users.findAll(pageable);
    }

    public User byId(UUID id) {
        return users.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user with id " + id));
    }
}
