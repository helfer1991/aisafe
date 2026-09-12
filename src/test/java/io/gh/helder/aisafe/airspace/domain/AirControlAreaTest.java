package io.gh.helder.aisafe.airspace.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import io.gh.helder.aisafe.common.domain.Coordinates;

class AirControlAreaTest {

    private static AirControlArea europe() {
        return area("EUR", "Europe", "30", "75", "0", "40");
    }

    private static AirControlArea area(String code, String name,
                                       String minLat, String maxLat,
                                       String minLon, String maxLon) {
        return new AirControlArea(
                new AreaCode(code), name,
                BoundingBox.of(minLat, maxLat, minLon, maxLon));
    }

    private static Coordinates at(String lat, String lon) {
        return new Coordinates(new BigDecimal(lat), new BigDecimal(lon));
    }

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        void assignsAnIdAndCreationTimestamp() {
            var area = europe();

            assertThat(area.id()).isNotNull();
            assertThat(area.createdAt()).isNotNull();
        }

        @Test
        void givesEachAreaADistinctIdentity() {
            assertThat(europe().id()).isNotEqualTo(europe().id());
        }

        @Test
        void trimsTheName() {
            var area = area("EUR", "  Europe  ", "30", "75", "0", "40");

            assertThat(area.name()).isEqualTo("Europe");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void rejectsBlankNames(String name) {
            assertThatThrownBy(() -> area("EUR", name, "30", "75", "0", "40"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name is required");
        }

        @Test
        void rejectsMissingCode() {
            assertThatThrownBy(() -> new AirControlArea(
                    null, "Europe", BoundingBox.of("30", "75", "0", "40")))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("area code is required");
        }

        @Test
        void rejectsMissingBoundary() {
            assertThatThrownBy(() -> new AirControlArea(
                    new AreaCode("EUR"), "Europe", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("boundary is required");
        }
    }

    @Nested
    @DisplayName("contains")
    class Contains {

        @ParameterizedTest(name = "{2} at {0},{1} is inside Europe")
        @CsvSource({
                "50.0264,  8.5431, Frankfurt",
                "52.5167, 13.4000, Berlin",
                "41.8044, 12.2508, Rome",
                "55.9726, 37.4146, Moscow"
        })
        void acceptsPointsWithinTheBoundary(String lat, String lon, String label) {
            assertThat(europe().contains(at(lat, lon)))
                    .as(label)
                    .isTrue();
        }

        @ParameterizedTest(name = "{2} at {0},{1} is outside Europe")
        @CsvSource({
                "41.2481, -8.6814, Porto (west of the prime meridian)",
                "25.7932, -80.2906, Miami (wrong hemisphere)",
                "-26.1392, 28.2460, Johannesburg (southern latitude)",
                "35.5523, 139.7800, Tokyo (east of 40 degrees)"
        })
        void rejectsPointsOutsideTheBoundary(String lat, String lon, String label) {
            assertThat(europe().contains(at(lat, lon)))
                    .as(label)
                    .isFalse();
        }

        @Test
        void includesTheSouthWestCorner() {
            assertThat(europe().contains(at("30", "0"))).isTrue();
        }

        @Test
        void excludesTheNorthEastCorner() {
            assertThat(europe().contains(at("75", "40"))).isFalse();
        }

        @Test
        void assignsAPointOnASharedEdgeToExactlyOneArea() {
            var west = area("WST", "West", "30", "75", "0", "20");
            var east = area("EST", "East", "30", "75", "20", "40");
            var onTheSeam = at("50", "20");

            assertThat(west.contains(onTheSeam)).isFalse();
            assertThat(east.contains(onTheSeam)).isTrue();
        }
    }

    @Nested
    @DisplayName("overlaps")
    class Overlaps {

        @Test
        void detectsPartialIntersection() {
            var other = area("OVL", "Overlapping", "40", "50", "5", "15");

            assertThat(europe().overlaps(other)).isTrue();
            assertThat(other.overlaps(europe())).isTrue();
        }

        @Test
        void detectsFullContainment() {
            var inner = area("INN", "Inside Europe", "45", "50", "10", "15");

            assertThat(europe().overlaps(inner)).isTrue();
            assertThat(inner.overlaps(europe())).isTrue();
        }

        @Test
        void ignoresDisjointAreas() {
            var africa = area("AFR", "Africa", "-40", "25", "-20", "40");

            assertThat(europe().overlaps(africa)).isFalse();
        }

        @Test
        void treatsSharedEdgesAsAdjacentRatherThanOverlapping() {
            var asia = area("ASI", "Asia", "0", "75", "40", "150");

            assertThat(europe().overlaps(asia)).isFalse();
        }

        @Test
        void doesNotReportAnAreaAsOverlappingItself() {
            var area = europe();

            assertThat(area.overlaps(area)).isFalse();
        }

        @Test
        void reportsOverlapBetweenTwoAreasWithIdenticalBoundaries() {
            // Distinct identities, same extent: a genuine conflict.
            assertThat(europe().overlaps(europe())).isTrue();
        }
    }

    @Nested
    @DisplayName("identity")
    class Identity {

        @Test
        void equalsItself() {
            var area = europe();

            assertThat(area).isEqualTo(area);
        }

        @Test
        void differsFromAnotherAreaWithTheSameCodeAndBoundary() {
            assertThat(europe()).isNotEqualTo(europe());
        }

        @Test
        void isNotEqualToOtherTypes() {
            assertThat(europe()).isNotEqualTo("EUR");
        }

        @Test
        void hasAReadableStringRepresentation() {
            assertThat(europe()).hasToString("AirControlArea[EUR]");
        }
    }
}
