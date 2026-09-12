package io.gh.helder.aisafe.identity.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aisafe.email")
public record EmailProperties(Set<String> allowedDomains) {
}
