package io.gh.helder.aisafe.identity.web.dto;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.gh.helder.aisafe.identity.domain.Role;

public record CreateUserRequest(
        @NotBlank @jakarta.validation.constraints.Email String email,
        @NotBlank @Size(min = 12, max = 100) String password,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Pattern(regexp = "\\+?[0-9 ]{6,20}") String phoneNumber,
        @NotEmpty Set<Role> roles,
        @NotNull @Future LocalDate clearanceExpiresOn,
        @NotNull @PastOrPresent LocalDate skillsAssessedOn) { }
