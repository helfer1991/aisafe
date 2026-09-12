package io.gh.helder.aisafe.airspace.domain;

public class DuplicateAreaCodeException extends RuntimeException {
    public DuplicateAreaCodeException(String message) {
        super(message);
    }
}