package io.gh.helder.aisafe.catalog.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAirportRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String town,
        @NotBlank @Size(max = 80)  String country,
        @NotBlank @Pattern(regexp = "(?i)[a-z]{3}") String iataCode,
        @NotBlank @Pattern(regexp = "(?i)[a-z]{4}") String icaoCode,
        @NotNull @DecimalMin("-90")  @DecimalMax("90")  BigDecimal latitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
        @NotNull Integer elevationM,
        @NotBlank @Pattern(regexp = "(?i)[a-z0-9]{2,10}") String areaCode) {
}
