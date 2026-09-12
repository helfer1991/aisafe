package io.gh.helder.aisafe.catalog.web;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.gh.helder.aisafe.catalog.service.AirportService;
import io.gh.helder.aisafe.catalog.web.dto.AirportResponse;
import io.gh.helder.aisafe.catalog.web.dto.CreateAirportRequest;

@RestController
@RequestMapping("/api/airports")
class AirportController {

    private final AirportService service;

    AirportController(AirportService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<AirportResponse> create(@Valid @RequestBody CreateAirportRequest request) {
        var airport = service.register(request);
        return ResponseEntity
                .created(URI.create("/api/airports/" + airport.icaoCode().value()))
                .body(AirportResponse.from(airport));
    }

    @GetMapping
    Page<AirportResponse> list(@PageableDefault(size = 20, sort = "icaoCode.value") Pageable pageable) {
        return service.findAll(pageable).map(AirportResponse::from);
    }

    @GetMapping("/{icaoCode}")
    AirportResponse byCode(@PathVariable String icaoCode) {
        return AirportResponse.from(service.byIcaoCode(icaoCode));
    }
}
