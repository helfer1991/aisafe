package io.gh.helder.aisafe.identity.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String refreshToken) { }
