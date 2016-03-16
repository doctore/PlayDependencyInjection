package org.play.dependencyinjection.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Identifies the current implementation of a particular interface
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DependencyInjectionQualifier {

	String value() default "";
}