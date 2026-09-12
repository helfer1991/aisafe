package io.gh.helder.aisafe.airspace.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.gh.helder.aisafe.airspace.domain.AirControlArea;

public record AreaResponse(
        UUID id,
        String code,
        String name,
        BigDecimal minLatitude,
        BigDecimal maxLatitude,
        BigDecimal minLongitude,
        BigDecimal maxLongitude) {

    public static AreaResponse from(AirControlArea area) {
        var b = area.boundary();
        return new AreaResponse(
                area.id(),
                area.code().value(),
                area.name(),
                b.minLatitude(),
                b.maxLatitude(),
                b.minLongitude(),
                b.maxLongitude());
    }
}
