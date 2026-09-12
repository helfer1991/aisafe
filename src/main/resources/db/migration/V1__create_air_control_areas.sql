CREATE TABLE air_control_areas (
    id              UUID PRIMARY KEY,
    area_code       VARCHAR(10)      NOT NULL UNIQUE,
    name            VARCHAR(120)     NOT NULL,
    min_latitude    NUMERIC(9,6)     NOT NULL,
    max_latitude    NUMERIC(9,6)     NOT NULL,
    min_longitude   NUMERIC(9,6)     NOT NULL,
    max_longitude   NUMERIC(9,6)     NOT NULL,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now(),
    version         BIGINT           NOT NULL DEFAULT 0,

    CONSTRAINT aca_code_format CHECK (area_code ~ '^[A-Z0-9]{2,10}$'),
    CONSTRAINT aca_lat_range   CHECK (min_latitude  BETWEEN -90 AND 90
                                  AND max_latitude  BETWEEN -90 AND 90),
    CONSTRAINT aca_lon_range   CHECK (min_longitude BETWEEN -180 AND 180
                                  AND max_longitude BETWEEN -180 AND 180),
    CONSTRAINT aca_non_zero_area CHECK (min_latitude  < max_latitude
                                    AND min_longitude < max_longitude)
);