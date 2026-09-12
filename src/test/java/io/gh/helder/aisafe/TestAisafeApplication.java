package io.gh.helder.aisafe;

import org.springframework.boot.SpringApplication;

public class TestAisafeApplication {

    public static void main(String[] args) {
        SpringApplication.from(AisafeApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
