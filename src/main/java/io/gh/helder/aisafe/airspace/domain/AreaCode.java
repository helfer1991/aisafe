package io.gh.helder.aisafe.airspace.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record AreaCode(@Column(name = "area_code", length = 10) String value) {

    private static final Pattern FORMAT = Pattern.compile("[A-Z0-9]{2,10}");

    public AreaCode {
        Objects.requireNonNull(value, "Area code is required");
        value = value.trim().toUpperCase();
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Area code must be between 2 and 10 letters, got: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
