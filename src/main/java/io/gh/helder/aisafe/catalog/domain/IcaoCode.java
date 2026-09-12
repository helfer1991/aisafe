package io.gh.helder.aisafe.catalog.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record IcaoCode(@Column(name = "icao_code", length = 4) String value) {

    private static final Pattern FORMAT = Pattern.compile("[A-Z]{4}");

    public IcaoCode {
        Objects.requireNonNull(value, "ICAO code is required");
        value = value.trim().toUpperCase();
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "ICAO code must be 4 letters, got: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
