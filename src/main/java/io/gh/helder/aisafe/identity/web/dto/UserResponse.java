package io.gh.helder.aisafe.identity.web.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import io.gh.helder.aisafe.identity.domain.Role;
import io.gh.helder.aisafe.identity.domain.User;

public record UserResponse(
        UUID id, String email, String name, String phoneNumber,
        Set<Role> roles, boolean enabled,
        LocalDate clearanceExpiresOn, LocalDate skillsAssessedOn,
        boolean clearanceValid, boolean skillsAssessmentCurrent) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.id(), user.email().value(), user.name(), user.phoneNumber(),
                user.roles(), user.enabled(),
                user.clearanceExpiresOn(), user.skillsAssessedOn(),
                user.clearanceValid(), user.skillsAssessmentCurrent());
    }
}
