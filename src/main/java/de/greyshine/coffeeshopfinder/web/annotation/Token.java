package de.greyshine.coffeeshopfinder.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used on String parameters to auto inject the token from a web frontend request.
 * Value will be null if no token can be obtained by the matching http header.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Deprecated // bot used anywhere
public @interface Token {

}
