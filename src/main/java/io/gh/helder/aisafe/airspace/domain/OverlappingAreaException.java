package io.gh.helder.aisafe.airspace.domain;

public class OverlappingAreaException extends RuntimeException {
    public OverlappingAreaException(String message) {
        super(message);
    }
}