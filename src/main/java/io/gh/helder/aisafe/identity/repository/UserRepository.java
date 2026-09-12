package io.gh.helder.aisafe.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.gh.helder.aisafe.identity.domain.Email;
import io.gh.helder.aisafe.identity.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
}