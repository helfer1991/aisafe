package io.gh.helder.aisafe.identity.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @jakarta.validation.constraints.Email String email,
        @NotBlank String password) { }