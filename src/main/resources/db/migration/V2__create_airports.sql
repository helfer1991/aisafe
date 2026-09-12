CREATE TABLE airports (
    id                  UUID PRIMARY KEY,
    name                VARCHAR(120)  NOT NULL,
    town                VARCHAR(120)  NOT NULL,
    country             VARCHAR(80)   NOT NULL,
    iata_code           VARCHAR(3)    NOT NULL UNIQUE,
    icao_code           VARCHAR(4)    NOT NULL UNIQUE,
    latitude            NUMERIC(9,6)  NOT NULL,
    longitude           NUMERIC(9,6)  NOT NULL,
    elevation_m         INTEGER       NOT NULL,
    air_control_area_id UUID          NOT NULL,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    version             BIGINT        NOT NULL DEFAULT 0,

    CONSTRAINT fk_airports_area FOREIGN KEY (air_control_area_id)
        REFERENCES air_control_areas (id),

    CONSTRAINT airports_iata_format CHECK (iata_code ~ '^[A-Z]{3}$'),
    CONSTRAINT airports_icao_format CHECK (icao_code ~ '^[A-Z]{4}$'),
    CONSTRAINT airports_lat_range   CHECK (latitude  BETWEEN  -90 AND  90),
    CONSTRAINT airports_lon_range   CHECK (longitude BETWEEN -180 AND 180)
);

CREATE INDEX idx_airports_area ON airports (air_control_area_id);