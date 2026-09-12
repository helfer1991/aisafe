package io.gh.helder.aisafe.catalog.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record IataCode(@Column(name = "iata_code", length = 3) String value) {
    public IataCode {
        Objects.requireNonNull(value, "IATA code required");
        value = value.trim().toUpperCase();
        if (!value.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Invalid IATA code: " + value);
        }
    }
}
