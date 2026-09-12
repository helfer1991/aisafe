package io.gh.helder.aisafe.catalog.domain;

public class AirportOutsideAreaException extends RuntimeException {
    public AirportOutsideAreaException(String message) {
        super(message);
    }
}