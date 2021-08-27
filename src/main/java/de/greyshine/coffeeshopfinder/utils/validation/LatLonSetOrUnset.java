package de.greyshine.coffeeshopfinder.utils.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = LatLonSetOrUnsetValidation.class)
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface LatLonSetOrUnset {

    String message() default
            "longitude and latitude differ being set and unset";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
