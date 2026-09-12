package io.gh.helder.aisafe.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Coordinates(
        @Column(precision = 9, scale = 6) BigDecimal latitude,
        @Column(precision = 9, scale = 6) BigDecimal longitude) {

    private static final BigDecimal MIN_LAT = new BigDecimal("-90");
    private static final BigDecimal MAX_LAT = new BigDecimal("90");
    private static final BigDecimal MIN_LON = new BigDecimal("-180");
    private static final BigDecimal MAX_LON = new BigDecimal("180");
    private static final int SCALE = 6;
    private static final double EARTH_RADIUS_M = 6_371_008.8;

    public Coordinates {
        Objects.requireNonNull(latitude, "latitude is required");
        Objects.requireNonNull(longitude, "longitude is required");

        if (latitude.compareTo(MIN_LAT) < 0 || latitude.compareTo(MAX_LAT) > 0) {
            throw new IllegalArgumentException("latitude out of range: " + latitude);
        }
        if (longitude.compareTo(MIN_LON) < 0 || longitude.compareTo(MAX_LON) > 0) {
            throw new IllegalArgumentException("longitude out of range: " + longitude);
        }

        latitude = latitude.setScale(SCALE, RoundingMode.HALF_UP);
        longitude = longitude.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Coordinates of(String latitude, String longitude) {
        return new Coordinates(new BigDecimal(latitude), new BigDecimal(longitude));
    }

    public static Coordinates of(double latitude, double longitude) {
        return new Coordinates(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
    }

    public double distanceTo(Coordinates other) {
        Objects.requireNonNull(other, "other is required");

        double lat1 = Math.toRadians(latitude.doubleValue());
        double lat2 = Math.toRadians(other.latitude.doubleValue());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(other.longitude.subtract(longitude).doubleValue());

        double a = Math.pow(Math.sin(dLat / 2), 2)
                 + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);

        return 2 * EARTH_RADIUS_M * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }

    /** Initial bearing in degrees from north, 0–360. */
    public double bearingTo(Coordinates other) {
        double lat1 = Math.toRadians(latitude.doubleValue());
        double lat2 = Math.toRadians(other.latitude.doubleValue());
        double dLon = Math.toRadians(other.longitude.subtract(longitude).doubleValue());

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2)
                 - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
