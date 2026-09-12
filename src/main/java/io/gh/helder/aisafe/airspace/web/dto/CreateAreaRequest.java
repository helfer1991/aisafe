package io.gh.helder.aisafe.airspace.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAreaRequest(
        @NotBlank @Pattern(regexp = "(?i)[a-z0-9]{2,10}") String code,
        @NotBlank @Size(max = 120) String name,
        @NotNull @DecimalMin("-90")  @DecimalMax("90")  BigDecimal minLatitude,
        @NotNull @DecimalMin("-90")  @DecimalMax("90")  BigDecimal maxLatitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal minLongitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal maxLongitude) {
}
