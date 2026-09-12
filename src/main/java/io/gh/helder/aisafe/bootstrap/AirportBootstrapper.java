package io.gh.helder.aisafe.bootstrap;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.gh.helder.aisafe.airspace.domain.AirControlArea;
import io.gh.helder.aisafe.airspace.domain.AreaCode;
import io.gh.helder.aisafe.airspace.repository.AirControlAreaRepository;
import io.gh.helder.aisafe.catalog.domain.Airport;
import io.gh.helder.aisafe.catalog.domain.IataCode;
import io.gh.helder.aisafe.catalog.domain.IcaoCode;
import io.gh.helder.aisafe.catalog.repository.AirportRepository;
import io.gh.helder.aisafe.common.domain.Coordinates;

@Component
@Profile("dev")
@Order(10)
class AirportBootstrapper implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AirportBootstrapper.class);
    private static final String SOURCE = "bootstrap/airports.xml";

    private final AirportRepository airports;
    private final AirControlAreaRepository areas;

    AirportBootstrapper(AirportRepository airports, AirControlAreaRepository areas) {
        this.airports = airports;
        this.areas = areas;
    }
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (airports.count() > 0) {
            log.info("Airports already present, skipping bootstrap");
            return;
        }

        List<Airport> parsed = new ArrayList<>();
        List<String> rejected = new ArrayList<>();

        for (AirportXml xml : read()) {
            try {
                parsed.add(toDomain(xml));
            } catch (IllegalArgumentException e) {
                rejected.add(xml.id + ": " + e.getMessage());
            }
        }

        airports.saveAll(parsed);
        log.info("Bootstrapped {} airports", parsed.size());

        if (!rejected.isEmpty()) {
            log.warn("Rejected {} airports: {}", rejected.size(), rejected);
        }
    }

    private List<AirportXml> read() throws Exception {
        var mapper = XmlMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        try (InputStream in = new ClassPathResource(SOURCE).getInputStream()) {
            return mapper.readValue(in, NetworkXml.class).airports;
        }
    }

    private Airport toDomain(AirportXml xml) {
        AirControlArea area = areas.findByCode(new AreaCode(xml.area))
                .orElseThrow(() -> new IllegalArgumentException(
                        "unknown air control area: " + xml.area));

        return new Airport(
                xml.name,
                xml.town,
                xml.country,
                new IataCode(xml.id),
                new IcaoCode(xml.icao),
                new Coordinates(
                        new BigDecimal(xml.location.latitude),
                        new BigDecimal(xml.location.longitude)),
                parseAltitude(xml.location.altitude),
                area);
    }

    private static int parseAltitude(String raw) {
        String digits = raw.replaceAll("[^0-9.\\-]", "").trim();
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("unparseable altitude: " + raw);
        }
        return new BigDecimal(digits).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class NetworkXml {
        @JacksonXmlElementWrapper(localName = "airport_list")
        @JacksonXmlProperty(localName = "airport")
        public List<AirportXml> airports;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AirportXml {
        @JacksonXmlProperty(isAttribute = true)
        public String id;
        @JacksonXmlProperty(isAttribute = true)
        public String icao;
        public String name;
        public String town;
        public String country;
        public LocationXml location;
        @JacksonXmlProperty(isAttribute = true)
        public String area;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class LocationXml {
        public String latitude;
        public String longitude;
        public String altitude;
    }
}
