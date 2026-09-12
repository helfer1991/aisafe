package io.gh.helder.aisafe.airspace.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import io.gh.helder.aisafe.common.domain.Coordinates;

@Embeddable
public record BoundingBox(
        @Column(name = "min_latitude",  precision = 9, scale = 6) BigDecimal minLatitude,
        @Column(name = "max_latitude",  precision = 9, scale = 6) BigDecimal maxLatitude,
        @Column(name = "min_longitude", precision = 9, scale = 6) BigDecimal minLongitude,
        @Column(name = "max_longitude", precision = 9, scale = 6) BigDecimal maxLongitude) {

    private static final BigDecimal LAT_LIMIT = new BigDecimal("90");
    private static final BigDecimal LON_LIMIT = new BigDecimal("180");
    private static final int SCALE = 6;

    public BoundingBox {
        Objects.requireNonNull(minLatitude,  "minLatitude is required");
        Objects.requireNonNull(maxLatitude,  "maxLatitude is required");
        Objects.requireNonNull(minLongitude, "minLongitude is required");
        Objects.requireNonNull(maxLongitude, "maxLongitude is required");

        requireWithin(minLatitude,  LAT_LIMIT, "minLatitude");
        requireWithin(maxLatitude,  LAT_LIMIT, "maxLatitude");
        requireWithin(minLongitude, LON_LIMIT, "minLongitude");
        requireWithin(maxLongitude, LON_LIMIT, "maxLongitude");

        if (minLatitude.compareTo(maxLatitude) >= 0) {
            throw new IllegalArgumentException(
                    "minLatitude must be strictly less than maxLatitude (zero-area boundary)");
        }
        if (minLongitude.compareTo(maxLongitude) >= 0) {
            throw new IllegalArgumentException(
                    "minLongitude must be strictly less than maxLongitude "
                    + "(zero-area boundary, or boundary crossing the antimeridian, "
                    + "which is not supported)");
        }

        minLatitude  = minLatitude.setScale(SCALE, RoundingMode.HALF_UP);
        maxLatitude  = maxLatitude.setScale(SCALE, RoundingMode.HALF_UP);
        minLongitude = minLongitude.setScale(SCALE, RoundingMode.HALF_UP);
        maxLongitude = maxLongitude.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private static void requireWithin(BigDecimal v, BigDecimal limit, String field) {
        if (v.abs().compareTo(limit) > 0) {
            throw new IllegalArgumentException(field + " out of range: " + v);
        }
    }

    public static BoundingBox of(String minLat, String maxLat, String minLon, String maxLon) {
        return new BoundingBox(new BigDecimal(minLat), new BigDecimal(maxLat),
                               new BigDecimal(minLon), new BigDecimal(maxLon));
    }

    /** Half-open containment: [min, max). A point on a shared edge belongs to one box only. */
    public boolean contains(Coordinates point) {
        return point.latitude().compareTo(minLatitude)  >= 0
            && point.latitude().compareTo(maxLatitude)  <  0
            && point.longitude().compareTo(minLongitude) >= 0
            && point.longitude().compareTo(maxLongitude) <  0;
    }

    /** Strict overlap: boxes that merely share an edge do not overlap. */
    public boolean overlaps(BoundingBox other) {
        return minLatitude.compareTo(other.maxLatitude)  < 0
            && maxLatitude.compareTo(other.minLatitude)  > 0
            && minLongitude.compareTo(other.maxLongitude) < 0
            && maxLongitude.compareTo(other.minLongitude) > 0;
    }
}
