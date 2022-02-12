package de.greyshine.coffeeshopfinder.web;

import lombok.Data;

@Data
public class LoginException extends RuntimeException {

    public final String login;
    public final String info;

    public LoginException(String login) {
        this(login, null);
    }

    public LoginException(String login, String info) {
        this.login = login;
        this.info = info;
    }
}
