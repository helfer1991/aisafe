package io.gh.helder.aisafe.airspace.domain;

public class AreaNotFoundException extends RuntimeException {
    public AreaNotFoundException(String message) {
        super(message);
    }
}