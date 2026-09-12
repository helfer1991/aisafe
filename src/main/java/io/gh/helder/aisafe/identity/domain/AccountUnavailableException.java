package io.gh.helder.aisafe.identity.domain;

public class AccountUnavailableException extends RuntimeException {
    public AccountUnavailableException(String message) {
        super(message);
    }
}
