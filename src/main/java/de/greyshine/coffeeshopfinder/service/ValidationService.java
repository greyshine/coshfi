package de.greyshine.coffeeshopfinder.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.Assert.notNull;


@Service
@Slf4j
public class ValidationService {

    public static final int PASSWORD_MINLENGTH = 6;
    public static final String PASSWORD_BLANK = "password.blank";
    public static final String PASSWORD_CRITERIA_MATCH = "password.bad_format";

    public static final Pattern PASSWORD_PATTERN_NUMBER = Pattern.compile(".*[0-9].*");
    public static final Pattern PASSWORD_PATTERN_SPECIALCHARACTER = Pattern.compile(".*[!\"§\\$%&/()=?+*#'<>,;.:\\-_@\\[\\|].*");

    public static final String LOGIN_BLANK = "login.blank";
    public static final Pattern LOGIN_PATTERN = Pattern.compile("[\\p{L}\\p{M}\\p{N}][\\p{L}\\p{M}\\p{N}-_+*]{0,23}[\\p{L})\\p{M}\\p{N}]");
    public static final String LOGIN_BAD_FORMAT = "login.bad_format";

    public static final String USER_BLANK = "user.blank";
    public static final Pattern USER_PATTERN = Pattern.compile("[\\p{L}\\p{M}\\p{N}][\\p{L}\\p{M}\\p{N}-_+&|/+ ]{0,23}[\\p{L})\\p{M}\\p{N}]");
    public static final String USER_BAD_FORMAT = "user.bad_format";

    public static final String EMAIL_BLANK = "email.blank";
    /**
     * https://stackoverflow.com/a/8204716/845117
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final String EMAIL_BAD_FORMAT = "email.bad_format";

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
        } else if (!password.equals(password.strip())) {
            throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
        } else if (password.length() < PASSWORD_MINLENGTH) {
            throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
        } else

            // check upper and lower
            if (password.equals(password.toLowerCase(Locale.ROOT)) || password.equals(password.toUpperCase(Locale.ROOT))) {
                log.debug("password mismatch: no upper- and lowercase");
                throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
            } else

                // have a number
                if (!PASSWORD_PATTERN_NUMBER.matcher(password).matches()) {
                    log.debug("password mismatch: no number");
                    throw new ValidationFailure(password, PASSWORD_CRITERIA_MATCH);
                } else

                    // have a special character
                    if (!PASSWORD_PATTERN_SPECIALCHARACTER.matcher(password).matches()) {
                        log.debug("password mismatch: no special character");
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
        if (isBlank(login)) {
            throw new ValidationFailure(login, LOGIN_BLANK);
        } else if (!LOGIN_PATTERN.matcher(login).matches()) {
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

        validateNotBlank(user, USER_BLANK);
        user = user.strip();

        if (!USER_PATTERN.matcher(user).matches()) {
            throw new ValidationFailure(user, USER_BAD_FORMAT);
        }

        if ("admin".equalsIgnoreCase(user)) {
        }
    }

    public void validateEmail(String email) throws ValidationFailure {
        validateNotBlank(email, EMAIL_BLANK);
        validatePattern(EMAIL_PATTERN, email, EMAIL_BAD_FORMAT);
    }

    public void validateNotBlank(String value, String message) throws ValidationFailure {
        if (isBlank(value)) {
            throw new ValidationFailure(value, isNotBlank(message) ? message : "value is blank");
        }
    }

    public void validateNotNull(String value, String message) throws ValidationFailure {
        if (value == null) {
            throw new ValidationFailure(value, isNotBlank(message) ? message : "value is blank");
        }
    }

    public void validatePattern(Pattern pattern, String value, String message) throws ValidationFailure {

        notNull(pattern, "Pattern must not be null");
        validateNotNull(value, message);

        if (!pattern.matcher(value).matches()) {
            throw new ValidationFailure(value, message != null ? message : "value does not match regex [value=" + value + ", regex=" + pattern.pattern() + "]");
        }
    }
}

