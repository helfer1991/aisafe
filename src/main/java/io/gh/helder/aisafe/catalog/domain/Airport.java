package io.gh.helder.aisafe.catalog.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import io.gh.helder.aisafe.airspace.domain.AirControlArea;
import io.gh.helder.aisafe.common.domain.Coordinates;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "airports")
public class Airport {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String town;

    @Column(nullable = false)
    private String country;

    @Embedded
    private IataCode iataCode;

    @Embedded
    private IcaoCode icaoCode;

    @Embedded
    private Coordinates coordinates;

    @Column(name = "elevation_m", nullable = false)
    private int elevationM;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "air_control_area_id", nullable = false)
    private AirControlArea airControlArea;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private long version;

    protected Airport() { }   // JPA only

    public Airport(String name, String town, String country,
                   IataCode iataCode, IcaoCode icaoCode,
                   Coordinates coordinates, int elevationM,
                   AirControlArea airControlArea) {
        this.id = UUID.randomUUID();
        this.name = requireText(name, "name");
        this.town = requireText(town, "town");
        this.country = requireText(country, "country");
        this.iataCode = Objects.requireNonNull(iataCode, "IATA code is required");
        this.icaoCode = Objects.requireNonNull(icaoCode, "ICAO code is required");
        this.coordinates = Objects.requireNonNull(coordinates, "coordinates are required");
        this.airControlArea = Objects.requireNonNull(airControlArea,
                "air control area is required");
        this.elevationM = elevationM;
        this.createdAt = Instant.now();

        if (!airControlArea.contains(coordinates)) {
            throw new AirportOutsideAreaException(
                    "Airport at " + coordinates + " is outside area " + airControlArea.code());
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    public UUID id()                       { return id; }
    public String name()                   { return name; }
    public String town()                   { return town; }
    public String country()                { return country; }
    public IataCode iataCode()             { return iataCode; }
    public IcaoCode icaoCode()             { return icaoCode; }
    public Coordinates coordinates()       { return coordinates; }
    public int elevationM()                { return elevationM; }
    public AirControlArea airControlArea()  { return airControlArea; }
    public Instant createdAt()             { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airport other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Airport[" + icaoCode + "]";
    }
}