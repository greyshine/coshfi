package de.greyshine.coffeeshopfinder.utils.validation;


import de.greyshine.coffeeshopfinder.utils.Latlon;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
@Slf4j
public class LatLonSetOrUnsetValidation implements ConstraintValidator<LatLonSetOrUnset, Latlon> {

    @Override
    public boolean isValid(Latlon latlon, ConstraintValidatorContext context) {
        return (latlon.getLat() == null) == (latlon.getLon() == null);
    }
}
