package io.gh.helder.aisafe.airspace.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.gh.helder.aisafe.airspace.domain.AirControlArea;
import io.gh.helder.aisafe.airspace.domain.AreaCode;

public interface AirControlAreaRepository extends JpaRepository<AirControlArea, UUID> {

    Optional<AirControlArea> findByCode(AreaCode code);

    boolean existsByCode(AreaCode code);

    @Query("""
           SELECT a FROM AirControlArea a
           WHERE a.boundary.minLatitude  < :maxLat
             AND a.boundary.maxLatitude  > :minLat
             AND a.boundary.minLongitude < :maxLon
             AND a.boundary.maxLongitude > :minLon
           """)
    List<AirControlArea> findOverlapping(@Param("minLat") BigDecimal minLat,
                                         @Param("maxLat") BigDecimal maxLat,
                                         @Param("minLon") BigDecimal minLon,
                                         @Param("maxLon") BigDecimal maxLon);

    @Query("""
           SELECT a FROM AirControlArea a
           WHERE :lat >= a.boundary.minLatitude  AND :lat < a.boundary.maxLatitude
             AND :lon >= a.boundary.minLongitude AND :lon < a.boundary.maxLongitude
           """)
    Optional<AirControlArea> findContaining(@Param("lat") BigDecimal lat,
                                            @Param("lon") BigDecimal lon);
}
