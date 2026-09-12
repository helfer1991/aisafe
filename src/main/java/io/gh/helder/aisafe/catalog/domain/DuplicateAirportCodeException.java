package io.gh.helder.aisafe.catalog.domain;

public class DuplicateAirportCodeException extends RuntimeException {
    public DuplicateAirportCodeException(String message) {
        super(message);
    }
}