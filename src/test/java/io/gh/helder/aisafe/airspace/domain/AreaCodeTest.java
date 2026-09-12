package io.gh.helder.aisafe.airspace.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AreaCodeTest {

    @Nested
    @DisplayName("accepts")
    class Accepts {

        @ParameterizedTest
        @ValueSource(strings = {"EU", "EUR", "NAT", "OCE", "ABCDEFGHIJ"})
        void validCodes(String input) {
            assertThat(new AreaCode(input).value()).isEqualTo(input);
        }

        @ParameterizedTest
        @ValueSource(strings = {"A1", "1A", "123", "EUR2026"})
        void digitsAsWellAsLetters(String input) {
            assertThat(new AreaCode(input).value()).isEqualTo(input);
        }

        @Test
        void exactlyTwoCharacters() {
            assertThat(new AreaCode("EU").value()).isEqualTo("EU");
        }

        @Test
        void exactlyTenCharacters() {
            assertThat(new AreaCode("ABCDEFGHIJ").value()).isEqualTo("ABCDEFGHIJ");
        }
    }

    @Nested
    @DisplayName("normalises")
    class Normalises {

        @ParameterizedTest(name = "\"{0}\" becomes EUR")
        @ValueSource(strings = {"eur", "Eur", "eUr", "EUR"})
        void toUpperCase(String input) {
            assertThat(new AreaCode(input).value()).isEqualTo("EUR");
        }

        @ParameterizedTest(name = "\"{0}\" becomes EUR")
        @ValueSource(strings = {"  EUR", "EUR  ", "  EUR  ", "\tEUR\n"})
        void bySurroundingWhitespace(String input) {
            assertThat(new AreaCode(input).value()).isEqualTo("EUR");
        }

        @Test
        void beforeValidating() {
            // " eur " is 5 characters raw but 3 once normalised, so it must pass.
            assertThat(new AreaCode(" eur ").value()).isEqualTo("EUR");
        }

        @Test
        void soThatDifferentlyWrittenCodesAreEqual() {
            assertThat(new AreaCode(" eur ")).isEqualTo(new AreaCode("EUR"));
        }

        @Test
        void consistentlyForHashing() {
            assertThat(new AreaCode("eur")).hasSameHashCodeAs(new AreaCode("EUR"));
        }
    }

    @Nested
    @DisplayName("rejects")
    class Rejects {

        @Test
        void nullValues() {
            assertThatThrownBy(() -> new AreaCode(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Area code is required");
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        void blankValues(String input) {
            assertThatThrownBy(() -> new AreaCode(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void codesShorterThanTwoCharacters() {
            assertThatThrownBy(() -> new AreaCode("E"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("E");
        }

        @Test
        void codesLongerThanTenCharacters() {
            assertThatThrownBy(() -> new AreaCode("ABCDEFGHIJK"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "\"{0}\" — {1}")
        @CsvSource({
                "EU-R,     hyphen",
                "EU R,     internal space",
                "EU_R,     underscore",
                "EU.R,     full stop",
                "'EU''R',  apostrophe",
                "EURÓ,     accented letter",
                "EU€,      currency symbol"
        })
        void codesContainingAnythingOtherThanLettersAndDigits(String input, String reason) {
            assertThatThrownBy(() -> new AreaCode(input))
                    .as(reason)
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void reportsTheOffendingValueInTheMessage() {
            assertThatThrownBy(() -> new AreaCode("TOO-LONG-AND-WRONG"))
                    .hasMessageContaining("TOO-LONG-AND-WRONG");
        }
    }

    @Nested
    @DisplayName("behaves as a value")
    class BehavesAsAValue {

        @Test
        void equalCodesAreInterchangeable() {
            assertThat(new AreaCode("NAT")).isEqualTo(new AreaCode("NAT"));
        }

        @Test
        void differentCodesAreNotEqual() {
            assertThat(new AreaCode("NAT")).isNotEqualTo(new AreaCode("EUR"));
        }

        @Test
        void printsTheBareCode() {
            assertThat(new AreaCode("EUR")).hasToString("EUR");
        }
    }
}
