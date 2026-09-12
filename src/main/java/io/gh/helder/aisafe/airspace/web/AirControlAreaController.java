package io.gh.helder.aisafe.airspace.web;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.gh.helder.aisafe.airspace.service.AirControlAreaService;
import io.gh.helder.aisafe.airspace.web.dto.AreaResponse;
import io.gh.helder.aisafe.airspace.web.dto.CreateAreaRequest;
import io.gh.helder.aisafe.common.domain.Coordinates;

@RestController
@RequestMapping("/api/air-control-areas")
class AirControlAreaController {

    private final AirControlAreaService service;

    AirControlAreaController(AirControlAreaService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<AreaResponse> create(@Valid @RequestBody CreateAreaRequest request) {
        var area = service.register(request);
        return ResponseEntity
                .created(URI.create("/api/air-control-areas/" + area.code().value()))
                .body(AreaResponse.from(area));
    }

    @GetMapping
    Page<AreaResponse> list(@PageableDefault(size = 20, sort = "code.value") Pageable pageable) {
        return service.findAll(pageable).map(AreaResponse::from);
    }

    @GetMapping("/{code}")
    AreaResponse byCode(@PathVariable String code) {
        return AreaResponse.from(service.byCode(code));
    }

    /** Which area covers a point? Useful for checking coverage before registering an airport. */
    @GetMapping("/containing")
    ResponseEntity<AreaResponse> containing(@RequestParam BigDecimal latitude,
                                            @RequestParam BigDecimal longitude) {
        return service.findContaining(new Coordinates(latitude, longitude))
                .map(AreaResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
