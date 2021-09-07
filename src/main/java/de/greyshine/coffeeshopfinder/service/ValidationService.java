package de.greyshine.coffeeshopfinder.service;

import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;


@Service
@Slf4j
public class ValidationService {

    public static final int PASSWORD_MINLENGTH = 6;
    public static final String PASSWORD_BLANK = "password.blank";
    public static final String PASSWORD_CRITERIA_MATCH = "password.bad_format";

    public static final Pattern PASSWORD_PATTERN_NUMBER = Pattern.compile( ".*[0-9].*" );
    public static final Pattern PASSWORD_PATTERN_SPECIALCHARACTER = Pattern.compile( ".*[!\"§\\$%&/()=?+*#'<>,;.:\\-_@\\[\\|].*" );

    public static final String LOGIN_BLANK = "login.blank";
    public static final Pattern LOGIN_PATTERN = Pattern.compile("[\\p{L}\\p{M}\\p{N}][\\p{L}\\p{M}\\p{N}-_+*]{0,23}[\\p{L})\\p{M}\\p{N}]");
    public static final String LOGIN_BAD_FORMAT = "login.bad_format";

    public static final String USER_BLANK = "user.blank";
    public static final Pattern USER_PATTERN = Pattern.compile("[\\p{L}\\p{M}\\p{N}][\\p{L}\\p{M}\\p{N}-_+&|/+ ]{0,23}[\\p{L})\\p{M}\\p{N}]");
    public static final String USER_BAD_FORMAT = "user.bad_format";

    public boolean isValidPassword(String password) {
        try {
            validatePassword(password);
            return true;
        } catch (ValidationFailure validationFailure) {
            return false;
        }
    }

    public void validatePassword(String password) throws ValidationFailure {

        if (isBlank(password)) {
            throw new ValidationFailure(password, PASSWORD_BLANK);
        } else if (!password.equals( password.trim() )) {
            throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
        } else

        if (password.length() < PASSWORD_MINLENGTH) {
            throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
        } else

        // check upper and lower
        if (password.equals(password.toLowerCase(Locale.ROOT)) || password.equals(password.toUpperCase(Locale.ROOT))) {
            log.debug( "password mismatch: no upper- and lowercase" );
            throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
        } else

        // have a number
        if ( !PASSWORD_PATTERN_NUMBER.matcher(password).matches() ) {
            log.debug( "password mismatch: no number" );
            throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
        } else

        // have a special character
        if ( !PASSWORD_PATTERN_SPECIALCHARACTER.matcher(password).matches() ) {
            log.debug( "password mismatch: no special character" );
            throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
        }
    }

    public boolean isValidLogin(String login) {

        try {
            validateLogin(login);
            return true;
        } catch (ValidationFailure validationFailure) {
            return false;
        }
    }

    public void validateLogin(String login) throws ValidationFailure {
        if ( isBlank(login) ) {
            throw new ValidationFailure(login, LOGIN_BLANK);
        } else if ( !LOGIN_PATTERN.matcher(login).matches() )   {
            throw new ValidationFailure(login, LOGIN_BAD_FORMAT);
        }
    }

    public boolean isValidUsername(String user) {

        try {
            validateUsername(user);
            return true;
        } catch (ValidationFailure validationFailure) {
            return false;
        }
    }

    public void validateUsername(String user) throws ValidationFailure {
        if ( isBlank(user) ) {
            throw new ValidationFailure(user, USER_BLANK);
        } else if ( !USER_PATTERN.matcher(user).matches() )   {
            throw new ValidationFailure(user, USER_BAD_FORMAT);
        }
    }
}

