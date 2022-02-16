package de.greyshine.coffeeshopfinder.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Tokenized {

    /**
     * Expected rights/rules to have for the invoking (person)
     * <p>
     * Have a comma separated list of values within one string, like "RIGHT_ONE,RIGHT_TWO,RULE_THREE" or one single "RIGHT_ONE"
     */
    String rrs() default "";
}
