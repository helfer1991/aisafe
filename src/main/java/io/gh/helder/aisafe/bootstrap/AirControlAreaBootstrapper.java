package io.gh.helder.aisafe.bootstrap;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.gh.helder.aisafe.airspace.domain.AirControlArea;
import io.gh.helder.aisafe.airspace.domain.AreaCode;
import io.gh.helder.aisafe.airspace.domain.BoundingBox;
import io.gh.helder.aisafe.airspace.repository.AirControlAreaRepository;

@Component
@Profile("dev")
@Order(0)
class AirControlAreaBootstrapper implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AirControlAreaBootstrapper.class);

    private final AirControlAreaRepository areas;

    AirControlAreaBootstrapper(AirControlAreaRepository areas) {
        this.areas = areas;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (areas.count() > 0) {
            log.info("Air control areas already present, skipping bootstrap");
            return;
        }

        var seed = List.of(
                area("NAT", "North Atlantic",   "25", "75", "-30",   "0"),
                area("EUR", "Europe",           "30", "75",   "0",  "40"),
                area("NAM", "North America",    "15", "75", "-140", "-50"),
                area("PAC", "Central Pacific",   "0", "40", "-180", "-140"),
                area("SAM", "South America",   "-60", "15",  "-90", "-30"),
                area("AFR", "Africa",          "-40", "25",  "-20",  "40"),
                area("ASI", "Asia",              "0", "75",   "40", "150"),
                area("OCE", "Oceania",         "-50",  "0",  "110", "180"));

        areas.saveAll(seed);
        log.info("Bootstrapped {} air control areas", seed.size());
    }

    private static AirControlArea area(String code, String name,
                                       String minLat, String maxLat,
                                       String minLon, String maxLon) {
        return new AirControlArea(
                new AreaCode(code),
                name,
                BoundingBox.of(minLat, maxLat, minLon, maxLon));
    }
}
