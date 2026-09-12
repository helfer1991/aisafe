package io.gh.helder.aisafe.catalog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.gh.helder.aisafe.airspace.domain.AirControlArea;
import io.gh.helder.aisafe.airspace.domain.AreaCode;
import io.gh.helder.aisafe.airspace.domain.AreaNotFoundException;
import io.gh.helder.aisafe.airspace.repository.AirControlAreaRepository;
import io.gh.helder.aisafe.catalog.domain.Airport;
import io.gh.helder.aisafe.catalog.domain.AirportNotFoundException;
import io.gh.helder.aisafe.catalog.domain.DuplicateAirportCodeException;
import io.gh.helder.aisafe.catalog.domain.IataCode;
import io.gh.helder.aisafe.catalog.domain.IcaoCode;
import io.gh.helder.aisafe.catalog.repository.AirportRepository;
import io.gh.helder.aisafe.catalog.web.dto.CreateAirportRequest;
import io.gh.helder.aisafe.common.domain.Coordinates;

@Service
@Transactional(readOnly = true)
public class AirportService {

    private final AirportRepository airports;
    private final AirControlAreaRepository areas;

    AirportService(AirportRepository airports, AirControlAreaRepository areas) {
        this.airports = airports;
        this.areas = areas;
    }

    @Transactional
    public Airport register(CreateAirportRequest request) {
        var iata = new IataCode(request.iataCode());
        var icao = new IcaoCode(request.icaoCode());

        if (airports.existsByIataCode(iata)) {
            throw new DuplicateAirportCodeException("IATA code already registered: " + iata);
        }
        if (airports.existsByIcaoCode(icao)) {
            throw new DuplicateAirportCodeException("ICAO code already registered: " + icao);
        }

        AirControlArea area = areas.findByCode(new AreaCode(request.areaCode()))
                .orElseThrow(() -> new AreaNotFoundException(
                        "No air control area with code " + request.areaCode()));

        // The Airport constructor verifies the coordinates fall inside this area.
        return airports.save(new Airport(
                request.name(), request.town(), request.country(),
                iata, icao,
                new Coordinates(request.latitude(), request.longitude()),
                request.elevationM(),
                area));
    }

    public Page<Airport> findAll(Pageable pageable) {
        return airports.findAll(pageable);
    }

    public Airport byIcaoCode(String code) {
        return airports.findByIcaoCode(new IcaoCode(code))
                .orElseThrow(() -> new AirportNotFoundException("No airport with ICAO code " + code));
    }
}
