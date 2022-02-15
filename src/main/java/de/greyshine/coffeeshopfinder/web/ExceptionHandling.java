package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.greyshine.coffeeshopfinder.utils.Utils.putIfAbsent;

@Slf4j
@ControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> onException(Exception exception) {

        log.error("return 500: {}, {}", exception.getClass().getCanonicalName(), exception.getMessage(), exception);
        return new ResponseEntity<>( exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> onException(RuntimeException exception) {

        log.error("return 500: {}, {}", exception.getClass().getCanonicalName(), exception.getMessage(), exception);
        return new ResponseEntity<>( exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> onException(ResponseStatusException exception) {

        log.debug("passing on a ResponseStatusException={}", exception);
        final var reason = Utils.getDefaultString(exception.getReason(), () -> exception.getMessage());
        return new ResponseEntity<>( reason, exception.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> onException(ConstraintViolationException constraintViolationException) {

        final Map<String, List<String>> validationFailures = new HashMap<>();

        constraintViolationException.getConstraintViolations().forEach(cv -> {

            final var key = cv.getPropertyPath().toString();
            putIfAbsent(validationFailures, key, () -> new ArrayList<>());
            validationFailures.get(key).add(cv.getMessage());
            log.debug("onException(ConstraintViolationException): {}\nname={}\nerror={}", cv, key, cv.getMessage());
        });

        return new ResponseEntity<>(validationFailures, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> onException(MissingServletRequestParameterException exception) {

        log.info("return 400: {}, {}", exception.getClass().getCanonicalName(), exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity onException(LoginException exception) {
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
}