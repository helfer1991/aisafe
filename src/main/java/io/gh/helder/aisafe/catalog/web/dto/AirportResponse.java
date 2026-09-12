package io.gh.helder.aisafe.catalog.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.gh.helder.aisafe.catalog.domain.Airport;

public record AirportResponse(
        UUID id, String name, String town, String country,
        String iataCode, String icaoCode,
        BigDecimal latitude, BigDecimal longitude,
        int elevationM) {

    public static AirportResponse from(Airport a) {
        return new AirportResponse(
                a.id(), a.name(), a.town(), a.country(),
                a.iataCode().value(), a.icaoCode().value(),
                a.coordinates().latitude(), a.coordinates().longitude(),
                a.elevationM());
    }
}
