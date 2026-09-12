package io.gh.helder.aisafe.catalog.domain;

public class AirportNotFoundException extends RuntimeException {
      public AirportNotFoundException(String message) {
        super(message);
    }
}
