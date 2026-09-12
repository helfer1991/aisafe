package io.gh.helder.aisafe.bootstrap;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.gh.helder.aisafe.identity.domain.Email;
import io.gh.helder.aisafe.identity.domain.Role;
import io.gh.helder.aisafe.identity.domain.User;
import io.gh.helder.aisafe.identity.repository.UserRepository;

@Component
@Profile("dev")
@Order(-10)
class UserBootstrapper implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UserBootstrapper.class);
    private static final String DEV_PASSWORD = "ChangeMe123!";

    private final UserRepository users;
    private final PasswordEncoder encoder;

    UserBootstrapper(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() > 0) {
            log.info("Users already present, skipping bootstrap");
            return;
        }

        LocalDate clearance = LocalDate.now().plusYears(2);
        LocalDate assessed = LocalDate.now().minusMonths(6);

        var seed = List.of(
                user("admin@aisafe.com",      "Ana Admin",      Role.ADMIN, clearance, assessed),
                user("backoffice@aisafe.com", "Bruno Backoffice", Role.BACKOFFICE_OPERATOR, clearance, assessed),
                user("weather@aisafe.com",    "Wanda Weather",  Role.WEATHER_PERSON, clearance, assessed),
                user("fco@eurocontrol.int",   "Filipe Control", Role.FLIGHT_CONTROL_OPERATOR, clearance, assessed),
                user("collab@tap.pt",         "Carla Collab",   Role.ATC_COLLABORATOR, clearance, assessed),
                user("pilot@tap.pt",          "Pedro Pilot",    Role.PILOT, clearance, assessed));

        users.saveAll(seed);
        log.info("Bootstrapped {} users (dev password: {})", seed.size(), DEV_PASSWORD);
    }

    private User user(String email, String name, Role role,
                      LocalDate clearance, LocalDate assessed) {
        return new User(new Email(email), encoder.encode(DEV_PASSWORD),
                name, "+351900000000", Set.of(role), clearance, assessed);
    }
}