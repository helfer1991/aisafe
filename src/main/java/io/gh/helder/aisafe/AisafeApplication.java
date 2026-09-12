package io.gh.helder.aisafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AisafeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AisafeApplication.class, args);
    }

}
