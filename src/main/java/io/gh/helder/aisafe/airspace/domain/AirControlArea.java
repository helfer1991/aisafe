package io.gh.helder.aisafe.airspace.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import io.gh.helder.aisafe.common.domain.Coordinates;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "air_control_areas")
public class AirControlArea {

    @Id
    private UUID id;

    @Embedded
    private AreaCode code;

    @Column(nullable = false)
    private String name;

    @Embedded
    private BoundingBox boundary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private long version;

    protected AirControlArea() { }   // JPA only

    public AirControlArea(AreaCode code, String name, BoundingBox boundary) {
        this.id = UUID.randomUUID();
        this.code = Objects.requireNonNull(code, "area code is required");
        this.boundary = Objects.requireNonNull(boundary, "boundary is required");

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        this.name = name.trim();
        this.createdAt = Instant.now();
    }

    public boolean contains(Coordinates point) {
        return boundary.contains(point);
    }

    public boolean overlaps(AirControlArea other) {
        return !equals(other) && boundary.overlaps(other.boundary);
    }

    public UUID id()             { return id; }
    public AreaCode code()       { return code; }
    public String name()         { return name; }
    public BoundingBox boundary(){ return boundary; }
    public Instant createdAt()   { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AirControlArea other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AirControlArea[" + code + "]";
    }
}