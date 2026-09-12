package io.gh.helder.aisafe.identity.web.dto;

public record TokenPair(String accessToken, String refreshToken, long expiresInSeconds) { }