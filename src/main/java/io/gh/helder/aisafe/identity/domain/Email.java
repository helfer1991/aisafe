package io.gh.helder.aisafe.identity.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;

@Embeddable
public record Email(String value) {

    private static final Pattern FORMAT =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    public Email {
        Objects.requireNonNull(value, "email is required");
        value = value.trim().toLowerCase();
        if (value.length() > 254 || !FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("invalid email: " + value);
        }
    }

    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }

    @Override
    public String toString() {
        return value;
    }
}
