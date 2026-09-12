package io.gh.helder.aisafe.identity.domain;

public class EmailDomainNotAllowedException extends RuntimeException {
    public EmailDomainNotAllowedException(String message) {
        super(message);
    }
}