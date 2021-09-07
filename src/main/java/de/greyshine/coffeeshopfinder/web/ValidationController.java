package de.greyshine.coffeeshopfinder.web;

import de.greyshine.coffeeshopfinder.service.ValidationFailure;
import de.greyshine.coffeeshopfinder.service.ValidationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: add a token to validation requests, so it is not abused by foreign usage
 */
@RestController
@Slf4j
public class ValidationController {

    public static final String FIELD_GLOBAL_PASSWORD = "global.password";

    @Autowired
    private ValidationService validationService;

    /**
     * Check token (not a logged-in token) for allowing to validate
     * in order not to get testet by unallowed facilities
     * @throws ValidationFailure
     */
    @PostMapping(value = "/api/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public void validate(@RequestBody Map<String,Object> objects ) throws ValidationFailure {

        for( String key : objects.keySet() ) {
            switch (key) {
                case FIELD_GLOBAL_PASSWORD:
                    validationService.validatePassword( (String)objects.get(key) );
                    return;
            }
        }

        log.warn( "nothing to do: {}", objects );
    }


    @ExceptionHandler(ValidationFailure.class)
    public ResponseEntity<String> onValidationFailure(ValidationFailure validationFailure) {
        return new ResponseEntity<>( validationFailure.getInfo(), HttpStatus.BAD_REQUEST);
    }

    @Data
    public static class ValidationRequestBody {

        private String field;
        private Object value;

        public boolean isField(String fieldName) {
            return StringUtils.equals( field, fieldName );
        }

        public String getValueString() {
            return value == null ? null : String.valueOf(value);
        }
    }

}
