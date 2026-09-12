package io.gh.helder.aisafe.catalog.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.gh.helder.aisafe.catalog.domain.Airport;
import io.gh.helder.aisafe.catalog.domain.IataCode;
import io.gh.helder.aisafe.catalog.domain.IcaoCode;

public interface AirportRepository extends JpaRepository<Airport, UUID> {

    Optional<Airport> findByIcaoCode(IcaoCode icaoCode);

    Optional<Airport> findByIataCode(IataCode iataCode);

    boolean existsByIcaoCode(IcaoCode icaoCode);

    boolean existsByIataCode(IataCode iataCode);
}
