package io.gh.helder.aisafe.identity.domain;

public enum Role {
    ADMIN,
    BACKOFFICE_OPERATOR,
    ATC_COLLABORATOR,
    PILOT,
    FLIGHT_CONTROL_OPERATOR,
    WEATHER_PERSON;

    public String authority() {
        return "ROLE_" + name();
    }
}