package io.gh.helder.aisafe.airspace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.gh.helder.aisafe.airspace.domain.AirControlArea;
import io.gh.helder.aisafe.airspace.domain.AreaCode;
import io.gh.helder.aisafe.airspace.domain.AreaNotFoundException;
import io.gh.helder.aisafe.airspace.domain.BoundingBox;
import io.gh.helder.aisafe.airspace.domain.DuplicateAreaCodeException;
import io.gh.helder.aisafe.airspace.domain.OverlappingAreaException;
import io.gh.helder.aisafe.airspace.repository.AirControlAreaRepository;
import io.gh.helder.aisafe.airspace.web.dto.CreateAreaRequest;
import io.gh.helder.aisafe.common.domain.Coordinates;

@Service
@Transactional(readOnly = true)
public class AirControlAreaService {

    private final AirControlAreaRepository areas;

    AirControlAreaService(AirControlAreaRepository areas) {
        this.areas = areas;
    }

    @Transactional
    public AirControlArea register(CreateAreaRequest request) {
        var code = new AreaCode(request.code());
        if (areas.existsByCode(code)) {
            throw new DuplicateAreaCodeException("Area code already registered: " + code);
        }

        var boundary = new BoundingBox(request.minLatitude(), request.maxLatitude(),
                                       request.minLongitude(), request.maxLongitude());
        var conflicts = areas.findOverlapping(boundary.minLatitude(), boundary.maxLatitude(),
                                              boundary.minLongitude(), boundary.maxLongitude());
        if (!conflicts.isEmpty()) {
            throw new OverlappingAreaException(
                    "Boundary overlaps existing areas: " + conflicts.stream()
                            .map(a -> a.code().value()).toList());
        }

        return areas.save(new AirControlArea(code, request.name(), boundary));
    }

    public Page<AirControlArea> findAll(Pageable pageable) {
        return areas.findAll(pageable);
    }

    public AirControlArea byCode(String code) {
        return areas.findByCode(new AreaCode(code))
                .orElseThrow(() -> new AreaNotFoundException("No area with code " + code));
    }

    public Optional<AirControlArea> findContaining(Coordinates point) {
        return areas.findContaining(point.latitude(), point.longitude());
    }

    @ExceptionHandler(AreaNotFoundException.class)
    ProblemDetail areaNotFound(AreaNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({DuplicateAreaCodeException.class, OverlappingAreaException.class})
    ProblemDetail areaConflict(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
